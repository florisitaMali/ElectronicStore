package Integration;

import DAO.CategoryDAO;
import DAO.ItemsDAO;
import DAO.ItemsDAOAdapter;
import DAO.ItemsRepository;
import Models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        CategoryDAO.addCategory(electronics);
        CategoryDAO.addCategory(laptop);
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


}
