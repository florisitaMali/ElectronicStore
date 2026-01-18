package Integration;

import DAO.CategoryDAO;
import DAO.DBConnection;
import DAO.ItemsDAO;
import DAO.SuppliersDAO;
import Models.Category;
import Models.Item;
import Models.Sector;
import Models.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

class ItemsIT {

    private Category category;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        // Create and insert Category
        category = new Category("Electronics", Sector.CAMERA);
        CategoryDAO.addCategory(category);

        // Create and insert Supplier
        supplier = new Supplier("TechSupplier", "address");
        SuppliersDAO.addSupplier(supplier);
    }

    @Test
    void shouldInsertItemWithValidCategoryAndSupplier() {
        Item item = new Item("Laptop", 10, category, supplier, 800.00, 1000.00, 5);
        assertDoesNotThrow(() -> ItemsDAO.addItem(item));
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
