package DAO;

import Models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Models.Role.ADMINISTRATOR;
import static Models.Role.CASHIER;

public class EmployeeDAO {
    public static ArrayList<Employee> getEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();

        String sql = """
        SELECT e.*, r.name AS role_name, s.name AS sector_name
        FROM employees e
        JOIN roles r ON e.role_id = r.id
        LEFT JOIN sectors s ON e.sector_id = s.id
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Role role = Role.valueOf(rs.getString("role_name"));

                if (role == CASHIER) {
                    Sector sector = Sector.valueOf(rs.getString("sector_name"));
                    Cashier cashier =new Cashier(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary"),
                            sector
                    );
                    for(Permission p : getPermissionsByEmployeeId(con, rs.getInt("id"))){
                        cashier.addPermission(p);
                    }

                    employees.add(cashier);
                } else if (role == ADMINISTRATOR) {
                    Administrator admin = new Administrator(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary")
                    );

                    employees.add(admin);
                }else{
                    Manager manager = new Manager(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary")
                    );
                    for(Permission p : getPermissionsByEmployeeId(con, rs.getInt("id")))
                    {
                        manager.addPermission(p);
                    }
                    for(Sector s : getManagerSectors(con, rs.getInt("id"))){
                        manager.addSector(s);
                    }
                    employees.add(manager);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public static void addEmployee(Employee e) throws NotValidUsername{

        if(usernameExists(e.getUsername())){
            throw new NotValidUsername("Username already exists: " + e.getUsername());
        }
        String sql = """
            INSERT INTO employees
            (first_name, last_name, username, password, email, phone, birth_date, salary, role_id, sector_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getName());
            ps.setString(2, e.getSurname());
            ps.setString(3, e.getUsername());
            ps.setString(4, e.getPassword());
            ps.setString(5, e.getEmail());
            ps.setString(6, e.getPhoneNumber());
            ps.setDate(7, Date.valueOf(e.getDateOfBirth()));
            ps.setDouble(8, e.getSalary());

            ps.setInt(9, getRoleId(e.getRole()));

            if (e instanceof Cashier cashier) {
                ps.setInt(10, getSectorId(cashier.getSector()));
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int employeeId = keys.getInt(1);
                savePermissions(con, employeeId, e.getAccessLevel());
                if (e instanceof Manager manager) {
                    saveManagerSectors(con, employeeId, manager.getSectors());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public static void deleteEmployee(Employee employee) {
        String sql = "DELETE FROM employees WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employee.getUsername());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Employee searchEmployee(String username, Role role) {

        String sql = """
        SELECT e.*, r.name AS role_name, s.name AS sector_name
        FROM employees e
        JOIN roles r ON e.role_id = r.id
        LEFT JOIN sectors s ON e.sector_id = s.id
        WHERE e.username = ? AND r.name = ?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, role.name());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                if (role == CASHIER) {
                    Cashier cashier =  new Cashier(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary"),
                            Sector.valueOf(rs.getString("sector_name"))
                    );
                    for(Permission p : getPermissionsByEmployeeId(con, rs.getInt("id"))){
                        cashier.addPermission(p);
                    }
                    return cashier;
                }

                if (role == ADMINISTRATOR) {
                    return new Administrator(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary")
                    );
                }

                if(role == Role.MANAGER){
                    Manager manager = new Manager(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary")
                    );
                    for(Permission p : getPermissionsByEmployeeId(con, rs.getInt("id"))){
                        manager.addPermission(p);
                    }
                    for(Sector s : getManagerSectors(con, rs.getInt("id"))){
                        manager.addSector(s);
                    }
                    return manager;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Administrator getAdministrator() {
        String sql = """
        SELECT e.*
        FROM employees e
        JOIN roles r ON e.role_id = r.id
        WHERE r.name = 'ADMINISTRATOR'
        LIMIT 1
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new Administrator(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getDouble("salary")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addAdministrator(Administrator admin) {

        String sql = """
        INSERT INTO employees
        (first_name, last_name, username, password, email, phone, birth_date, salary, role_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, admin.getName());
            ps.setString(2, admin.getSurname());
            ps.setString(3, admin.getUsername());
            ps.setString(4, admin.getPassword());
            ps.setString(5, admin.getEmail());
            ps.setString(6, admin.getPhoneNumber());
            ps.setDate(7, Date.valueOf(admin.getDateOfBirth()));
            ps.setDouble(8, admin.getSalary());
            ps.setInt(9, getRoleId(ADMINISTRATOR));

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Employee> getEmployeesOfSectors(ArrayList<Sector> sectors) {

        ArrayList<Employee> result = new ArrayList<>();
        if (sectors == null || sectors.isEmpty()) return result;

        String placeholders = String.join(",", Collections.nCopies(sectors.size(), "?"));

        String sql = """
        SELECT e.*, s.id AS sector_id, s.name AS sector_name
        FROM employees e
        JOIN roles r ON e.role_id = r.id
        JOIN sectors s ON e.sector_id = s.id
        WHERE r.name = 'CASHIER'
        AND s.name IN (%s)
    """.formatted(placeholders);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < sectors.size(); i++) {
                ps.setString(i + 1, sectors.get(i).name());
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Sector sector = Sector.valueOf(rs.getString("sector_name"));
                if (!"CASHIER".equals(rs.getString("role_name"))){
                    continue;
                }

                Cashier cashier = new Cashier(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getDouble("salary"),
                        sector
                );
                for(Permission p : getPermissionsByEmployeeId(con, rs.getInt("id"))){
                    cashier.addPermission(p);
                }
                result.add(cashier);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static int getRoleId(Role role) throws SQLException {
        String sql = "SELECT id FROM roles WHERE name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role.name());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Role not found: " + role);
    }

    private static int getSectorId(Sector sector) throws SQLException {
        String sql = "SELECT id FROM sectors WHERE name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sector.name());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Sector not found: " + sector);
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM employees WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void savePermissions(Connection con, int employeeId, List<Permission> permissions)
            throws SQLException {

        String sql = """
    INSERT INTO employee_permissions (employee_id, permission_id)
    VALUES (?, ?)
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Permission p : permissions) {
                ps.setInt(1, employeeId);
                ps.setInt(2, getPermissionId(con, p));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static int getPermissionId(Connection con, Permission permission)
            throws SQLException {

        String sql = "SELECT id FROM permissions WHERE name = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, permission.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Permission not found: " + permission);
    }

    private static List<Permission> getPermissionsByEmployeeId(Connection con, int employeeId)
            throws SQLException {

        List<Permission> permissions = new ArrayList<>();

        String sql = """
            SELECT p.name
            FROM permissions p
            JOIN employee_permissions ep ON ep.permission_id = p.id
            WHERE ep.employee_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                permissions.add(Permission.valueOf(rs.getString("name")));
            }
        }
        return permissions;
    }


    public static List<Permission> defaultPermissions(Role role) {
        return switch (role) {
            case CASHIER -> List.of(
                    Permission.GENERATE_PRINTABLE_BILL,
                    Permission.RECEIVE_ALERTS_IF_ITEM_NOT_EXIST_OR_OUT_OF_STOCK,
                    Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY
            );
            case MANAGER -> List.of(
                    Permission.GENERATE_PRINTABLE_BILL,
                    Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY,
                    Permission.ADD_ITEMS_TO_STOCK,
                    Permission.GENERATE_TOTAL_COST_INCOME,
                    Permission.ACCESS_STATISTICS_ABOUT_SOLD_AND_PURCHASED_ITEMS
            );
            default -> List.of(Permission.values());
        };
    }

    private static List<Sector> getManagerSectors(Connection con, int employeeId)
            throws SQLException {

        List<Sector> sectors = new ArrayList<>();

        String sql = """
        SELECT s.name
        FROM sectors s
        JOIN employee_sectors es ON es.sector_id = s.id
        WHERE es.employee_id = ?
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sectors.add(Sector.valueOf(rs.getString("name")));
            }
        }
        return sectors;
    }

    private static void saveManagerSectors(
            Connection con,
            int employeeId,
            List<Sector> sectors
    ) throws SQLException {

        String sql = """
        INSERT INTO employee_sectors (employee_id, sector_id)
        VALUES (?, ?)
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Sector s : sectors) {
                ps.setInt(1, employeeId);
                ps.setInt(2, getSectorId(s));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public static void updateEmployee(Employee employee) {
        String sql = """
        UPDATE employees
        SET name = ?,
            surname = ?,
            username = ?,
            password = ?,
            date_of_birth = ?,
            phone = ?,
            email = ?,
            salary = ?,
            role = ?,
            access_level = ?
        JOIN roles r ON employees.role_id = r.id
        WHERE username = ? AND r.name = role
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setString(2, employee.getSurname());
            ps.setString(3, employee.getUsername());
            ps.setString(4, employee.getPassword());
            ps.setDate(5, Date.valueOf(employee.getDateOfBirth()));
            ps.setString(6, employee.getPhoneNumber());
            ps.setString(7, employee.getEmail());
            ps.setDouble(8, employee.getSalary());
            ps.setString(9, employee.getRole().name());

            // Convert permissions to comma-separated string
            String permissions = String.join(",", employee.getAccessLevel().stream().map(Enum::name).toList());
            ps.setString(10, permissions);

            ps.setString(11, employee.getUsername());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
