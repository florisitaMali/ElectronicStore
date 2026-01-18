package Integration;

import DAO.*;
import Models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SupplierIT {

    private Supplier supplier;
    private Category category;

    @BeforeEach
    void setUp() {
        Supplier.setItemsDAO(new ItemsDAOAdapter());

        category = new Category("Electronics", Sector.ELECTRONICS);
        CategoryDAO.addCategory(category);

        supplier = new Supplier("Tech Distributors", "Tirana");
        SuppliersDAO.addSupplier(supplier);

        Item item1 = new Item("Laptop", 30, category, supplier, 700.00, 950.00, 5);

        Item item2 = new Item("Smartphone", 50, category, supplier, 400.00, 650.00, 10);

        ItemsDAO.addItem(item1);
        ItemsDAO.addItem(item2);
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
