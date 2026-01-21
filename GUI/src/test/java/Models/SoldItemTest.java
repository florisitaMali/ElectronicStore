package Models;

import FakeClasses.FakeItemsDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SoldItemTest {
    private SoldItem soldItem;

    @Test
    void testConstructor_valid(){
        //FakeItemsDAO has 2 items with names Item1 and Item2 and each with quatity 10
        //Therefore search quantity returns the Item with name Item1
        SoldItem.setItemsDAO(new FakeItemsDAO());
        // As sold quantity is less then the actual quantity then the sold item should be created successfully
        assertDoesNotThrow(() -> {
            SoldItem soldItem = new SoldItem("Item1", 2);

            // Optionally, check internal state
            assertEquals("Item1", soldItem.getItemName());
            assertEquals(2, soldItem.getSoldQuantity());
        });
    }

    @Test
    void testConstuctor_insufficientStock(){
        SoldItem.setItemsDAO(new FakeItemsDAO(){
            @Override
            public Item searchItem(String name) throws ItemNotFoundException {
                // Return an item with limited stock for testing
                return new FakeClasses.FakeItem("Item1", 5); // Only 5 in stock
            }
        });

        Exception ex = assertThrows(ItemNotAvailableException.class, () -> {
            //It requires 20 but only 5 in stock
            new SoldItem("Item1", 20);
        });
        assertTrue(ex.getMessage().contains("Item not available"));
    }

    @Test
    void testConstructor_itemNotFound(){
        //The seachItem method always throws ItemNotFoundException
        SoldItem.setItemsDAO(new FakeItemsDAO(){
            @Override
            public Item searchItem(String name) throws ItemNotFoundException {
                throw new ItemNotFoundException("Item not found: " + name);
            }
        });

        //Attempting to create a SoldItem with a non-existent item name
        Exception ex = assertThrows(ItemNotFoundException.class, () -> {
            new SoldItem("NonExistentItem", 2);
        });
        assertTrue(ex.getMessage().contains("Item not found"));
    }


    @Test
    void testGetSoldQuantity() {
        SoldItem.setItemsDAO(new FakeItemsDAO());
        soldItem = new SoldItem("Item1", 2);
        assertEquals(2, soldItem.getSoldQuantity());
    }

    @Test
    void testSetSoldQuantity_valid(){
        SoldItem.setItemsDAO(new FakeItemsDAO());
        //We create a SoldItem with 0 sold quantity initially not to throw any exception
        soldItem = new SoldItem("Item1", 0);
        //Now we set the sold quantity to 5 when there are 10 in stock
        soldItem.setQuantity(10);
        soldItem.setSoldQuantity(5);
        //Verify
        assertEquals(5, soldItem.getSoldQuantity());
        assertEquals(5, soldItem.getQuantity()); //remaining quantity
    }

    @Test
    void testSetSoldQuantity_exceedsStock() {
        SoldItem.setItemsDAO(new FakeItemsDAO());
        //We create a SoldItem with 0 sold quantity initially not to throw any exception
        soldItem = new SoldItem("Item1", 0);
        soldItem.setQuantity(1); //only 1 in stock
        //Now we try to set the sold quantity to 5 which exceeds the stock
        Exception ex = assertThrows(ItemNotAvailableException.class, () -> soldItem.setSoldQuantity(5));
        assertTrue(ex.getMessage().contains("not enough stock"));
    }
}
