package DAO;

import Models.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ItemsDAOTest {

    private Category testCategory;
    private Supplier testSupplier;

    @BeforeEach
    void setup() {
        cleanTables();
        testCategory = new Category("Electronics", Sector.ELECTRONICS);
        CategoryDAO.addCategory(testCategory);
        testSupplier = new Supplier("BestSupplier", "123 Supplier St.");
        SuppliersDAO.addSupplier(testSupplier);
    }

    @Test
    void addItem_unique_itemIsAdded() {
        Item item = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        ItemsDAO.addItem(item);

        Item retrieved = ItemsDAO.searchItem("Laptop");
        assertNotNull(retrieved);
        assertEquals("Laptop", retrieved.getItemName());
        assertEquals(10, retrieved.getQuantity());
        assertEquals(testCategory.getName(), retrieved.getItemCategory().getName());
        assertEquals(testSupplier.getSupplierName(), retrieved.getItemSupplier().getSupplierName());
    }

    @Test
    void addItem_duplicateItem_printsMessage() {
        Item item1 = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        ItemsDAO.addItem(item1);
        Item item2 = new Item("Laptop", 5, testCategory, testSupplier, 400, 650, 30);
        assertDoesNotThrow(() -> ItemsDAO.addItem(item2));
    }

    @Test
    void getAllItems_returnsNoItem() {
        List<Item> items = ItemsDAO.getAllItems();
        assertEquals(0, items.size());
    }

    @Test
    void getAllItems_returnsCorrectItems() {
        Item item1 = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        Item item2 = new Item("Phone", 20, testCategory, testSupplier, 300, 500, 100);
        ItemsDAO.addItem(item1);
        ItemsDAO.addItem(item2);

        List<Item> items = ItemsDAO.getAllItems();
        assertEquals(2, items.size());
    }

    @Test
    void searchItem_existingItem_returnsItem() {
        Item item = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        ItemsDAO.addItem(item);

        Item result = ItemsDAO.searchItem("Laptop");
        assertNotNull(result);
        assertEquals("Laptop", result.getItemName());
    }

    @Test
    void searchItem_partialName_returnsItem() {
        Item item = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        ItemsDAO.addItem(item);

        Item result = ItemsDAO.searchItem("Lap");
        assertNotNull(result);
        assertEquals("Laptop", result.getItemName());
    }

    @Test
    void searchItem_nonExisting_returnsNull() {
        Item result = ItemsDAO.searchItem("NonExist");
        assertNull(result);
    }

    @Test
    void updateItem_changesValues() {
        Item item = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        ItemsDAO.addItem(item);

        Item retrieved = ItemsDAO.searchItem("Laptop");
        retrieved.setQuantity(15);
        retrieved.setSellingPrice(750);
        ItemsDAO.updateItem(retrieved);

        Item updated = ItemsDAO.searchItem("Laptop");
        assertEquals(15, updated.getQuantity());
        assertEquals(750, updated.getSellingPrice());
    }

    @Test
    void updateItem_noExisting_doNothing() {
        Item item = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        assertDoesNotThrow(()->ItemsDAO.updateItem(item));
    }

    @Test
    void softDeleteItem_itemIsHidden() {
        Item item = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        ItemsDAO.addItem(item);

        Item retrieved = ItemsDAO.searchItem("Laptop");
        ItemsDAO.softDeleteItem(retrieved);

        List<Item> allItems = ItemsDAO.getAllItems();
        assertTrue(allItems.isEmpty());
    }

    @Test
    void deleteItem_itemIsRemoved() {
        Item item = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        ItemsDAO.addItem(item);

        ItemsDAO.deleteItem(item);
        Item result = ItemsDAO.searchItem("Laptop");
        assertNull(result);
    }

    @Test
    void deleteItem_notExist(){
        Item item = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        ItemsDAO.deleteItem(item);
        Item result = ItemsDAO.searchItem("Laptop");
        assertNull(result);
    }

    @Test
    void getItemsPurchasedStatistics_returnsCorrectData() {
        Item item = new Item("Laptop", 10, testCategory, testSupplier, 500, 700, 50);
        ItemsDAO.addItem(item);
        int itemId = ItemsDAO.searchItem("Laptop").getId();

        Bill bill = new Bill(EmployeeDAO.getAdministrator());
        bill.addSoldItems(new SoldItem("Laptop", 3));
        BillDAO.saveBill(bill);
        Map<String, Integer> stats = ItemsDAO.getItemsPurchasedStatistics(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertTrue(stats.containsKey("Laptop"));
        assertEquals(3, stats.get("Laptop"));
    }

    @Test
    void getItemsPurchasedStatistics_returnsEmptyMap() {
        Map<String, Integer> stats = ItemsDAO.getItemsPurchasedStatistics(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

    private void cleanTables() {
        String[] sqls = {
                "DELETE FROM bill_items",
                "DELETE FROM bills",
                "DELETE FROM items",
                "DELETE FROM categories",
                "DELETE FROM suppliers"
        };
        try (Connection con = DBConnection.getConnection()) {
            for (String sql : sqls) {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
            }
        } catch (Exception ignored) {}
    }
}
