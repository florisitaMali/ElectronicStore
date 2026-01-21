package Integration;

import DAO.*;
import Models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CategoryIT {

    private Category electronics;
    private Category laptop;
    private ItemsRepository itemsRepository;

    @BeforeEach
    void setUp() {
        electronics = new Category("Electronics", Sector.ELECTRONICS);
        laptop = new Category("Laptop", Sector.COMPUTERS);

        Supplier supplier1 = new Supplier("TechSupplier");
        Supplier supplier2 = new Supplier("GadgetWorld");

        Item item1 = new Item("Smartphone", 30, electronics, supplier1, 300, 500, 10);
        Item item2 = new Item("Tablet", 20, electronics, supplier2, 200, 350, 5);

        SuppliersDAO.addSupplier(supplier1);
        SuppliersDAO.addSupplier(supplier2);

        CategoryDAO.addCategory(electronics);
        CategoryDAO.addCategory(laptop);

        ItemsDAO.addItem(item1);
        ItemsDAO.addItem(item2);
    }

    @Test
    void testGetItemsInThisCategory_returnsOnlyElectronicsItems() {
        ArrayList<Item> electronicsItems = electronics.getItemsInThisCategory();

        assertNotNull(electronicsItems);
        assertFalse(electronicsItems.isEmpty());

        for (Item item : electronicsItems) {
            assertEquals("Electronics", item.getItemCategory().getName());
        }
    }

    @Test
    void testGetItemsInThisCategory_doesNotReturnOtherCategoryItems() {
        ArrayList<Item> foodItems = laptop.getItemsInThisCategory();

        for (Item item : foodItems) {
            assertEquals("Food", item.getItemCategory().getName());
        }
    }

    @Test
    void testEquals_sameCategoryName_shouldBeEqual() {
        Category anotherElectronics = new Category("Electronics", Sector.ELECTRONICS);

        assertEquals(electronics, anotherElectronics);
    }

    @Test
    void testEquals_differentCategoryName_shouldNotBeEqual() {
        assertNotEquals(electronics, laptop);
    }

    @Test
    void testToString_returnsCategoryName() {
        assertEquals("Electronics", electronics.toString());
    }

    @Test
    void testGetSector() {
        assertEquals(Sector.ELECTRONICS, electronics.getSector());
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
