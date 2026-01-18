package Integration;

import DAO.*;
import Models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class SoldItemIT {

    @BeforeEach
    void setUp() {
        SoldItem.setItemsDAO(new ItemsDAOAdapter());

        Category category = new Category("Electronics", Sector.ELECTRONICS);
        CategoryDAO.addCategory(category);

        Supplier supplier = new Supplier("LocalSupplier", "Adress 123");
        SuppliersDAO.addSupplier(supplier);

        Item item = new Item("Milk", 20, category, supplier, 0.60, 1.00, 5);

        ItemsDAO.addItem(item);
    }

    @Test
    void shouldCreateSoldItemWhenEnoughQuantityExists() throws Exception {
        SoldItem soldItem = new SoldItem("Milk", 5);

        assertEquals("Milk", soldItem.getItemName());
        assertEquals(5, soldItem.getSoldQuantity());
        assertEquals(1.00, soldItem.getSellingPrice());
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughQuantity() {
        assertThrows(ItemNotAvailableException.class, () -> new SoldItem("Milk", 100));
    }

    @Test
    void shouldThrowExceptionWhenItemDoesNotExist() {
        assertThrows(ItemNotFoundException.class, () -> new SoldItem("NonExistingItem", 2));
    }

    @AfterEach
    public void deleteCategories() {
        executeDelete("DELETE FROM ITEMS");
        executeDelete("DELETE FROM CATEGORIES");
        executeDelete("DELETE FROM SUPPLIERS");
    }

    private void executeDelete(String query) {
        try (Connection conn = DBConnection.getConnection(); var ps = conn.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to clean up categories: " + e.getMessage());
        }
    }
}
