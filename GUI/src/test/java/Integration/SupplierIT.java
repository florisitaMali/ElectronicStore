package Integration;

import DAO.*;
import Models.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;

import DAO.DBConnection;

import static org.junit.jupiter.api.Assertions.*;

class SupplierIT {

    private Supplier supplier;
    private Category category;

    @BeforeEach
    void setUp() {
        Supplier.setItemsDAO(new ItemsDAOAdapter());
        try {
            category = new Category("Electronics" + System.currentTimeMillis(), Sector.ELECTRONICS);
            CategoryDAO.addCategory(category);

            supplier = new Supplier("Tech Distributors", "Tirana");
            SuppliersDAO.addSupplier(supplier);

            Item item1 = new Item("Laptop" + System.currentTimeMillis(), 30, category, supplier, 700.00, 950.00, 5);

            Item item2 = new Item("Smartphone" + System.currentTimeMillis(), 50, category, supplier, 400.00, 650.00, 10);

            ItemsDAO.addItem(item1);
            ItemsDAO.addItem(item2);
        } catch (Exception e) {
        }
    }

    @Test
    void shouldReturnAllProductsForSupplier() {
        ArrayList<Item> products = supplier.getProducts();

        assertNotNull(products);
        assertEquals(2, products.size());

        assertTrue(products.stream().allMatch(item -> item.getItemSupplier().getSupplierName().equalsIgnoreCase(supplier.getSupplierName())));
    }

    @Test
    void shouldReturnEmptyListWhenSupplierHasNoProducts() {
        Supplier emptySupplier = new Supplier("Peripheral Supplies", "Durres");
        SuppliersDAO.addSupplier(emptySupplier);

        ArrayList<Item> products = emptySupplier.getProducts();

        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void supplierPropertiesShouldReflectValues() {
        assertEquals("Tech Distributors", supplier.getSupplierName());
        assertEquals("Tirana", supplier.getAddress());

        supplier.setSupplierName("Advanced Tech Distributors");
        supplier.setAddress("Vlore");

        assertEquals("Advanced Tech Distributors", supplier.getSupplierName());
        assertEquals("Vlore", supplier.getAddress());
    }

    @AfterEach
    void cleanUpDatabaseAfterAll() {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (var deleteItems = conn.prepareStatement(
                    "DELETE FROM items " +
                            "WHERE supplier_id IN (" +
                            "   SELECT id FROM suppliers WHERE name LIKE 'Tech Distributors%'" +
                            ")"
            )) {
                deleteItems.executeUpdate();
            }

            // 2. Delete suppliers
            try (var deleteSuppliers = conn.prepareStatement(
                    "DELETE FROM suppliers WHERE name LIKE 'Tech Distributors%'"
            )) {
                deleteSuppliers.executeUpdate();
            }

            // 3. Delete categories
            try (var deleteCategories = conn.prepareStatement(
                    "DELETE FROM categories WHERE name LIKE 'Electronics%'"
            )) {
                deleteCategories.executeUpdate();
            }

            conn.commit();
            System.out.println("Cleaned up SupplierIT test data successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
