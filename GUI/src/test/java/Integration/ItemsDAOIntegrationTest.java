package Integration;

import DAO.DBConnection;
import DAO.ItemsDAO;
import Models.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemsDAOIntegrationTest {

    private static Category testCategory;
    private static Supplier testSupplier;
    private static Item testItem;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Insert a sector if not exists (assuming id=1 exists for ELECTRONICS)
            ResultSet rs = stmt.executeQuery("SELECT id FROM sectors WHERE name='ELECTRONICS'");
            if (!rs.next()) {
                stmt.execute("INSERT INTO sectors(name) VALUES ('ELECTRONICS')");
            }

            // Insert category safely
            stmt.execute("INSERT IGNORE INTO categories(name, sector) VALUES ('TEST_CATEGORY', 1)");

            // Insert supplier safely
            stmt.execute("INSERT IGNORE INTO suppliers(name, address) VALUES ('TEST_SUPPLIER', '123 Test Ave')");
        }

        // Use existing Sector enum
        testCategory = new Category("TEST_CATEGORY", Sector.COMPUTERS);
        testSupplier = new Supplier("TEST_SUPPLIER", "123 Test Ave");
        testItem = new Item("TEST_ITEM_1", 50, testCategory, testSupplier, 100.0, 150.0, 20);
    }

    @Test
    @Order(1)
    public void testAddItem() {
        ItemsDAO.addItem(testItem);
        Item fetchedItem = ItemsDAO.searchItem("TEST_ITEM_1");

        assertNotNull(fetchedItem);
        assertEquals(testItem.getItemName(), fetchedItem.getItemName());
        assertEquals(testItem.getQuantity(), fetchedItem.getQuantity());
        assertEquals(testItem.getItemCategory().getName(), fetchedItem.getItemCategory().getName());
        assertEquals(testItem.getItemSupplier().getSupplierName(), fetchedItem.getItemSupplier().getSupplierName());
    }

    @Test
    @Order(2)
    public void testGetAllItems() {
        ArrayList<Item> items = ItemsDAO.getAllItems();
        assertFalse(items.isEmpty());
        assertTrue(items.stream().anyMatch(item -> item.getItemName().equals("TEST_ITEM_1")));
    }

    @Test
    @Order(3)
    public void testUpdateItem() {
        Item itemToUpdate = ItemsDAO.searchItem("TEST_ITEM_1");
        itemToUpdate.setQuantity(75);
        itemToUpdate.setSellingPrice(175.0);

        ItemsDAO.updateItem(itemToUpdate);

        Item updatedItem = ItemsDAO.searchItem("TEST_ITEM_1");
        assertEquals(75, updatedItem.getQuantity());
        assertEquals(175.0, updatedItem.getSellingPrice());
    }

    @Test
    @Order(4)
    public void testGetItemId() {
        Item item = ItemsDAO.searchItem("TEST_ITEM_1");
        int id = ItemsDAO.getItemId(new SoldItem(item.getItemName(), item.getQuantity()));
        assertTrue(id > 0);
    }

    @Test
    @Order(5)
    public void testGetItemsPurchasedStatistics() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);

        Map<String, Integer> stats = ItemsDAO.getItemsPurchasedStatistics(start, end);
        assertNotNull(stats);
        assertTrue(stats.isEmpty()); // no bills inserted yet
    }

    @Test
    @Order(6)
    public void testSoftDeleteItem() {
        Item itemToUpdate = ItemsDAO.searchItem("TEST_ITEM_1");
        itemToUpdate.setQuantity(75);
        itemToUpdate.setSellingPrice(175.0);

        ItemsDAO.updateItem(itemToUpdate);

        Item updatedItem = ItemsDAO.searchItem("TEST_ITEM_1");
        assertEquals(75, updatedItem.getQuantity());
    }

    @Test
    @Order(7)
    public void testDeleteItem() {
        // Re-insert item
        ItemsDAO.addItem(testItem);

        Item item = ItemsDAO.searchItem("TEST_ITEM_1");
        ItemsDAO.deleteItem(item);

        Item deletedItem = ItemsDAO.searchItem("TEST_ITEM_1");
        assertNull(deletedItem);
    }

    @AfterAll
    public static void cleanupDatabase() throws Exception {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Remove only test data
            stmt.execute("DELETE FROM items WHERE name LIKE 'TEST_%'");
            stmt.execute("DELETE FROM categories WHERE name LIKE 'TEST_%'");
            stmt.execute("DELETE FROM suppliers WHERE name LIKE 'TEST_%'");
        }
    }
}
