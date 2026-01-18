package DAO;

import Models.Bill;
import Models.Employee;
import Models.Role;
import Models.SoldItem;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillDAO{

    private static final String BILL_FILES_DIR =
            "src/main/resources/com/example/gui/bills/";

    private static EmployeeDAO employeeDAO = new EmployeeDAO();
    private static ItemsDAO itemsDAO = new ItemsDAO();

    public static void setEmployeeDAO(EmployeeDAO dao) {
        employeeDAO = dao;
    }

    public static void setItemsDAO(ItemsDAO dao) {
        itemsDAO = dao;
    }

    public static void saveBill(Bill bill) {
        if (bill.getTotalPrice() <= 0) return;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            String billSql = """
            INSERT INTO bills (bill_number, sale_date, employee_id, total)
            VALUES (?, ?, ?, ?)
        """;

            int billId;
            try (PreparedStatement ps = con.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS)) {
                long billNumber = System.currentTimeMillis();
                ps.setLong(1, billNumber);

                ps.setTimestamp(2, Timestamp.valueOf(bill.getSaleDate()));

                int empId = getEmployeeId(con, bill.getEmployee());
                if (empId == 0) throw new SQLException("Employee not found in DB");
                ps.setInt(3, empId);

                ps.setDouble(4, bill.getTotalPrice());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Failed to create bill");
                    billId = keys.getInt(1);
                }
            }

            String itemsSql = """
            INSERT INTO bill_items (bill_id, item_id, quantity, price)
            VALUES (?, ?, ?, ?)
        """;

            try (PreparedStatement ps = con.prepareStatement(itemsSql)) {
                for (SoldItem s : bill.getSoldItems()) {
                    int itemId = ItemsDAO.getItemId(s);
                    if (itemId == 0) throw new SQLException("Item not found in DB: " + s.getItemName());

                    ps.setInt(1, billId);
                    ps.setInt(2, itemId);
                    ps.setInt(3, s.getSoldQuantity());
                    ps.setDouble(4, s.getSellingPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();

            createBillFile(bill);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===================== READ BILLS ===================== */

    public static ArrayList<Bill> getAllBills(LocalDate start, LocalDate end) {

        ArrayList<Bill> bills = new ArrayList<>();

        String sql = """
            SELECT b.*, e.username
            FROM bills b
            JOIN employees e ON b.employee_id = e.id
            WHERE DATE(b.sale_date) BETWEEN ? AND ?
            ORDER BY b.sale_date
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Employee emp = employeeDAO.searchEmployee(
                        rs.getString("username"),
                        Role.CASHIER
                );

                Bill bill = new Bill(emp);
                setBillData(bill, rs, con);
                bills.add(bill);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    /* ===================== STATISTICS ===================== */

    public static Map<String, Integer> getItemsSoldStatistics(
            LocalDate start,
            LocalDate end) {

        Map<String, Integer> stats = new HashMap<>();

        String sql = """
            SELECT i.name, SUM(bi.quantity) qty
            FROM bill_items bi
            JOIN items i ON bi.item_id = i.id
            JOIN bills b ON bi.bill_id = b.id
            WHERE DATE(b.sale_date) BETWEEN ? AND ?
            GROUP BY i.name
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("name"), rs.getInt("qty"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /* ===================== HELPERS ===================== */

    private static void setBillData(
            Bill bill,
            ResultSet rs,
            Connection con) throws SQLException {

        bill.getSoldItems().addAll(getBillItems(con, rs.getInt("id")));
    }

    private static List<SoldItem> getBillItems(Connection con, int billId)
            throws SQLException {

        List<SoldItem> items = new ArrayList<>();

        String sql = """
            SELECT i.name, bi.quantity, bi.price, i.purchased_price, i.purchased_date
            FROM bill_items bi
            JOIN items i ON bi.item_id = i.id
            WHERE bi.bill_id = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                items.add(new SoldItem(
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("purchased_price"),
                        rs.getDate("purchased_date").toLocalDate()
                ));
            }
        }
        return items;
    }

    private static int getEmployeeId(Connection con, Employee e)
            throws SQLException {

        String sql = "SELECT id FROM employees WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getUsername());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        throw new SQLException("Employee not found");
    }

    private static void createBillFile(Bill bill) throws IOException {

        File dir = new File(BILL_FILES_DIR);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(
                BILL_FILES_DIR +
                        "bill_" + bill.getBillNumber() + ".txt"
        );

        try (PrintWriter out = new PrintWriter(file)) {
            out.println(bill.printBill());
        }
    }


    public static ArrayList<Bill> getAllBills() {
        ArrayList<Bill> bills = new ArrayList<>();

        String sql = """
            SELECT b.*, e.username, r.name AS role
            FROM bills b
            JOIN employees e ON b.employee_id = e.id
            JOIN roles r ON e.role_id = r.id
            ORDER BY b.sale_date
        """;

        System.out.println("Getting all bills...");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String roleStr = rs.getString("role"); //get role from DB
                Role role = Role.valueOf(roleStr);

                Employee emp = employeeDAO.searchEmployee(username, role);
                if (emp == null) continue; //skip if employee not found

                Bill bill = new Bill(emp);
                setBillData(bill, rs, con);
                bills.add(bill);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public static LocalDateTime getLastDate() {
        return LocalDateTime.now();
    }

    public static long getNumberOfBills() {
        String sql = "SELECT COUNT(*) AS total FROM bills WHERE DATE(sale_date) = CURDATE()";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<Bill> getDayBills(Employee emp) {
        return getAllBills(LocalDate.now(), LocalDate.now());
    }
}
