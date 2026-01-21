package DAO;

import Models.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BillDAOTest {

    private static Employee validEmployee;
    private static Item testItem1;
    private static Item testItem2;
    private static String itemName = "TEST_ITEM_1";

    @BeforeEach
    void setup() {
        clearDatabase();
        insertBaseData();
    }

    @Test
    void saveBill_totalZeroOrNegative_DoNotSave() {
        Bill bill = new Bill(validEmployee);
        int before = BillDAO.getAllBills().size();
        assertDoesNotThrow(() -> BillDAO.saveBill(bill));
        assertEquals(before, BillDAO.getAllBills().size());
    }

    @Test
    void saveBill_employeeDoesNotExist_DoNotSave() {
        Employee nonExisting = new Cashier("NonExist", "NonExist", "nonext", "pass", "nxt@test.com", "123", LocalDate.of(1998, 10, 2), 300.0, Models.Sector.COMPUTERS);
        Bill bill = new Bill(nonExisting);
        bill.addSoldItems(new SoldItem(testItem1.getItemName(), 1));
        int before = BillDAO.getAllBills().size();
        assertDoesNotThrow(() -> BillDAO.saveBill(bill));
        assertEquals(before, BillDAO.getAllBills().size());
    }

    @Test
    void saveBill_validData_allSaved_andFileCreated() {
        Bill bill = new Bill(validEmployee);
        bill.addSoldItems(new SoldItem(testItem1.getItemName(), 2));

        assertDoesNotThrow(() -> BillDAO.saveBill(bill));

        List<Bill> bills = BillDAO.getAllBills();
        assertEquals(1, bills.size());

        File billFile = new File("bills/bill_" + bills.get(0).getBillNumber() + ".txt");
        assertTrue(billFile.exists());
    }


    @Test
    void getAllBills_noBills_returnsEmptyList() {
        clearDatabase();
        List<Bill> bills = BillDAO.getAllBills();
        assertTrue(bills.isEmpty());
    }

    @Test
    void getAllBills_withBills_returnsList() {
        saveOneValidBill();
        List<Bill> bills = BillDAO.getAllBills();
        assertEquals(1, bills.size());
    }

    @Test
    void getItemsSoldStatistics_startDateNull_throwsException() {
        assertThrows(RuntimeException.class, () -> BillDAO.getItemsSoldStatistics(null, LocalDate.now()));
    }

    @Test
    void getItemsSoldStatistics_endDateNull_throwsException() {
        assertThrows(RuntimeException.class, () -> BillDAO.getItemsSoldStatistics(LocalDate.now(), null));
    }

    @Test
    void getItemsSoldStatistics_startAfterEnd_returnsEmptyMap() {
        Map<String, Integer> stats = BillDAO.getItemsSoldStatistics(LocalDate.now(), LocalDate.now().minusDays(1));

        assertTrue(stats.isEmpty());
    }

    @Test
    void getItemsSoldStatistics_validDates_noBills_returnsEmptyMap() {
        clearDatabase();

        Map<String, Integer> stats = BillDAO.getItemsSoldStatistics(LocalDate.now().minusDays(5), LocalDate.now());

        assertTrue(stats.isEmpty());
    }

    @Test
    void getItemsSoldStatistics_billsOutOfRange_returnsEmptyMap() {
        saveBillWithDate(LocalDateTime.now().minusDays(10));

        Map<String, Integer> stats = BillDAO.getItemsSoldStatistics(LocalDate.now().minusDays(2), LocalDate.now());

        assertTrue(stats.isEmpty());
    }

    @Test
    void getItemsSoldStatistics_validBills_returnsCorrectStats() {
        saveOneValidBill();

        Map<String, Integer> stats = BillDAO.getItemsSoldStatistics(LocalDate.now().minusDays(1), LocalDate.now());

        assertEquals(1, stats.size());
        assertEquals(2, stats.get(testItem1.getItemName()));
    }

    @Test
    void getNumberOfBills_noBills_returnsZero() {
        clearDatabase();
        assertEquals(0, BillDAO.getNumberOfBills());
    }

    @Test
    void getNumberOfBills_onlyTodayBills_returnsCount() {
        saveOneValidBill();
        saveOneValidBill();

        assertEquals(2, BillDAO.getNumberOfBills());
    }

    @Test
    void getNumberOfBills_previousAndTodayBills_returnsTodayCount() {
        saveBillWithDate(LocalDateTime.now().minusDays(1));
        saveOneValidBill();

        assertEquals(1, BillDAO.getNumberOfBills());
    }

    @Test
    void getDayBills_noBills_returnsEmptyList() {
        clearDatabase();
        assertTrue(BillDAO.getDayBills(validEmployee).isEmpty());
    }

    @Test
    void getDayBills_onlyTodayBills_returnsList() {
        saveOneValidBill();
        List<Bill> bills = BillDAO.getDayBills(validEmployee);

        assertEquals(1, bills.size());
    }

    @Test
    void getDayBills_previousAndTodayBills_returnsTodayOnly() {
        saveBillWithDate(LocalDateTime.now().minusDays(1));
        saveOneValidBill();

        List<Bill> bills = BillDAO.getDayBills(validEmployee);
        assertEquals(1, bills.size());
    }

    private void insertBaseData() {
        try (Connection con = DBConnection.getConnection(); Statement stmt = con.createStatement()) {

            // Insert test category
            stmt.execute("INSERT INTO categories(id, name, sector) VALUES (1000, 'TEST_CATEGORY', 1000) ON DUPLICATE KEY UPDATE name=name");

            // Insert test supplier
            stmt.execute("INSERT INTO suppliers(id, name, address) VALUES (1000, 'TEST_SUPPLIER', '123 Test Ave') ON DUPLICATE KEY UPDATE name=name");
        } catch (Exception ex) {

        }

        // Add a test cashier if not exists
        validEmployee = EmployeeDAO.searchEmployee("cashier_test", Role.CASHIER);
        if (validEmployee == null) {
            validEmployee = new Cashier("Cashier", "Test", "cashier_test", "pass123", "cashier@test.com", "1234567890", LocalDate.of(1995, 2, 2), 2000, Sector.CAMERA);
            EmployeeDAO.addEmployee(validEmployee);
        }

        // Add test items
        testItem1 = ItemsDAO.searchItem("TEST_ITEM_1");
        if (testItem1 == null) {
            testItem1 = new Item("TEST_ITEM_1", 50, new Category("TEST_CATEGORY", Sector.COMPUTERS), new Supplier("TEST_SUPPLIER", "123 Test Ave"), 100, 150, 20);
            ItemsDAO.addItem(testItem1);
        }

        testItem2 = ItemsDAO.searchItem("TEST_ITEM_2");
        if (testItem2 == null) {
            testItem2 = new Item("TEST_ITEM_2", 30, new Category("TEST_CATEGORY", Sector.CAMERA), new Supplier("TEST_SUPPLIER", "123 Test Ave"), 50, 100, 10);
            ItemsDAO.addItem(testItem2);
        }
    }

    @AfterEach
    void deleteDatabase() {
        clearDatabase();
    }

    private void saveOneValidBill() {
        Bill bill = new Bill(validEmployee);
        bill.addSoldItems(new SoldItem(itemName, 2));
        BillDAO.saveBill(bill);
    }

    private void saveBillWithDate(LocalDateTime date) {
        Bill bill = new Bill(validEmployee) {
            @Override
            public LocalDateTime getSaleDate() {
                return date;
            }
        };

        bill.addSoldItems(new SoldItem(itemName, 1));
        BillDAO.saveBill(bill);
    }

    private void clearDatabase() {
        try (Connection c = DBConnection.getConnection()) {
            c.prepareStatement("DELETE FROM bill_items").executeUpdate();
            c.prepareStatement("DELETE FROM bills").executeUpdate();
            c.prepareStatement("DELETE FROM items").executeUpdate();
            c.prepareStatement("DELETE FROM suppliers").executeUpdate();
            c.prepareStatement("DELETE FROM categories").executeUpdate();
            c.prepareStatement("DELETE FROM employees").executeUpdate();
        } catch (Exception ignored) {
        }
    }
}