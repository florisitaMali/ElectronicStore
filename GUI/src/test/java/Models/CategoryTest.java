package Models;

import FakeClasses.FakeItemsDAO;
import org.junit.jupiter.api.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testConstructorAndGetters() {
        Sector sector = Sector.CAMERA;
        Category category = new Category("Electronics", sector);

        assertEquals("Electronics", category.getName());
        assertEquals(sector, category.getSector());
    }

    @Test
    void testSetters() {
        Sector sector1 = Sector.AUDIO_EQUIPMENT;
        Sector sector2 = Sector.COMPUTERS;
        Category category = new Category("Electronics", sector1);

        category.setName("Books");
        category.setSector(sector2);

        assertEquals("Books", category.getName());
        assertEquals(sector2, category.getSector());
    }

    @Test
    void testEquals() {
        Category c1 = new Category("Electronics", Sector.COMPUTERS);
        Category c2 = new Category("Electronics", Sector.COMPUTERS);
        Category c3 = new Category("Books", Sector.CAMERA);

        assertTrue(c1.equals(c2));
        assertFalse(c1.equals(c3));
    }

    @Test
    void testGetItemsInThisCategory_returnsCorrectItems() {
        //Create the category with name FakeCategory and sector CAMERA to match to the fake items
        // returned by FakeItemsDAO
        Category category = new Category("FakeCategory", Sector.CAMERA);
        //Both items returned by FakeItemsDAO is a FakeItem which belongs to FakeCategory
        category.setItemsDAO(new FakeItemsDAO());
        //Retrieve items in this category
        ArrayList<Item> items = category.getItemsInThisCategory();

        //Verify that both items are returned
        assertEquals(2, items.size());
        //Verify that each item belongs to the correct category and sector
        for(Item item : items) {
            assertEquals("FakeCategory", item.getItemCategory().getName());
            assertEquals(Sector.CAMERA, item.getItemCategory().getSector());
        }
    }

    @Test
    void testGetItemsInThisCategory_noMatchingItems() {
        //Create a category that does not match any items in FakeItemsDAO
        Category category = new Category("NonExistentCategory", Sector.AUDIO_EQUIPMENT);
        category.setItemsDAO(new FakeItemsDAO());
        ArrayList<Item> items = category.getItemsInThisCategory();
        //Verify that no items are returned
        assertEquals(0, items.size());
        // Also verify that the returned list is not null
        assertNotNull(items);
    }

    @Test
    void testToString() {
        //toString should return the category name
        Category category = new Category("Electronics", Sector.COMPUTERS);
        assertEquals("Electronics", category.toString());
    }
}
