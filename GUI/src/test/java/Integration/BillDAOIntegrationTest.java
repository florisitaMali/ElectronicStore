package Integration;

import DAO.BillDAO;
import DAO.DBConnection;
import DAO.EmployeeDAO;
import DAO.ItemsDAO;
import Models.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BillDAOIntegrationTest {

    private static Employee testCashier;
    private static Item testItem1;
    private static Item testItem2;
    private static Bill testBill;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.execute("INSERT INTO sectors(id, name) VALUES (1000, 'CAMERA') ON DUPLICATE KEY UPDATE name=name");
            stmt.execute("INSERT INTO categories(id, name, sector) VALUES (1000, 'TEST_CATEGORY', 1000) ON DUPLICATE KEY UPDATE name=name");
            stmt.execute("INSERT INTO suppliers(id, name, address) VALUES (1000, 'TEST_SUPPLIER', '123 Test Ave') ON DUPLICATE KEY UPDATE name=name");
        }

        testCashier = EmployeeDAO.searchEmployee("cashier_test", Role.CASHIER);
        if (testCashier == null) {
            testCashier = new Cashier(
                    "Cashier", "Test", "cashier_test", "pass123", "cashier@test.com", "1234567890",
                    LocalDate.of(1995, 2, 2), 2000, Sector.CAMERA
            );
            EmployeeDAO.addEmployee(testCashier);
        }

        testItem1 = ItemsDAO.searchItem("TEST_ITEM_1");
        if (testItem1 == null) {
            testItem1 = new Item(
                    "TEST_ITEM_1",
                    50,
                    new Category("TEST_CATEGORY", Sector.CAMERA),
                    new Supplier("TEST_SUPPLIER", "123 Test Ave"),
                    100, 150, 20
            );
            ItemsDAO.addItem(testItem1);
        }

        testItem2 = ItemsDAO.searchItem("TEST_ITEM_2");
        if (testItem2 == null) {
            testItem2 = new Item(
                    "TEST_ITEM_2",
                    30,
                    new Category("TEST_CATEGORY", Sector.CAMERA),
                    new Supplier("TEST_SUPPLIER", "123 Test Ave"),
                    50, 100, 10
            );
            ItemsDAO.addItem(testItem2);
        }

        testBill = new Bill(testCashier);
        testBill.addSoldItems(new SoldItem(
                testItem1.getItemName(),
                5,
                testItem1.getSellingPrice(),
                testItem1.getPurchasedPrice(),
                testItem1.getPurchasedDate()
        ));
        testBill.addSoldItems(new SoldItem(
                testItem2.getItemName(),
                3,
                testItem2.getSellingPrice(),
                testItem2.getPurchasedPrice(),
                testItem2.getPurchasedDate()
        ));
    }

    @Test
    @Order(1)
    public void testSaveBill() {
        BillDAO.saveBill(testBill);

        ArrayList<Bill> bills = BillDAO.getAllBills(LocalDate.now().minusDays(1), LocalDate.now());
        assertFalse(bills.isEmpty());

        boolean found = bills.stream().anyMatch(b -> b.getBillNumber() == testBill.getBillNumber());
        assertTrue(found);
    }

    @Test
    @Order(2)
    public void testGetItemsSoldStatistics() {
        Map<String, Integer> stats = BillDAO.getItemsSoldStatistics(LocalDate.now(), LocalDate.now());

        assertNotNull(stats);
        assertTrue(stats.containsKey(testItem1.getItemName()));
        assertTrue(stats.containsKey(testItem2.getItemName()));
        assertEquals(5, stats.get(testItem1.getItemName()));
        assertEquals(3, stats.get(testItem2.getItemName()));
    }

    @Test
    @Order(3)
    public void testBillFileCreated() {
        File billFile = new File("src/main/resources/com/example/gui/bills/bill_" + testBill.getBillNumber() + ".txt");
        assertTrue(billFile.exists(), "Bill file should exist: " + billFile.getAbsolutePath());
    }

    @AfterAll
    public static void cleanupDatabase() throws Exception {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate("DELETE FROM bill_items WHERE bill_id IN (SELECT id FROM bills WHERE bill_number = " + testBill.getBillNumber() + ")");
            stmt.executeUpdate("DELETE FROM bills WHERE bill_number = " + testBill.getBillNumber());
            stmt.executeUpdate("DELETE FROM items WHERE name LIKE 'TEST_%'");
        }

        File billFile = new File("src/main/resources/com/example/gui/bills/bill_" + testBill.getBillNumber() + ".txt");
        if (billFile.exists()) {
            billFile.delete();
        }
    }
}
