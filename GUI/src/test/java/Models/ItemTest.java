package Models;

import FakeClasses.FakeCategory;
import FakeClasses.FakeSupplier;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item item;

    @BeforeEach
    void setup() throws Exception {
        Category fakeCategory = new FakeCategory("Electronics", Sector.MOBILE_DEVICES);
        Supplier fakeSupplier = new FakeSupplier("Supplier1");

        item = new Item(
                "Laptop",
                10,
                fakeCategory,
                fakeSupplier,
                500,
                700,
                1
        );
    }

    @Test
    void testGetters() {
        //Test all getters
        assertEquals("Laptop", item.getItemName());
        assertEquals(10, item.getQuantity());
        assertEquals(500, item.getPurchasedPrice());
        assertEquals(700, item.getSellingPrice());
        assertEquals(1, item.getStockLimit());
        assertEquals("Electronics", item.getItemCategory().getName());
        assertEquals("Supplier1", item.getItemSupplier().getSupplierName());
        assertNotNull(item.getPurchasedDate());
    }

    @Test
    void testSetters() {
        //Test all setters and verify with getters
        item.setItemName("Phone");
        assertEquals("Phone", item.getItemName());

        item.setQuantity(5);
        assertEquals(5, item.getQuantity());

        item.setItemCategory(new FakeCategory("Fake", Sector.CAMERA));
        assertEquals("Fake", item.getItemCategory().getName());

        item.setItemSupplier(new FakeSupplier("FakeSupplier"));
        assertEquals("FakeSupplier", item.getItemSupplier().getSupplierName());

        item.setPurchasedPrice(300);
        assertEquals(300, item.getPurchasedPrice());

        item.setSellingPrice(400);
        assertEquals(400, item.getSellingPrice());

        item.setPurchasedDate(LocalDate.of(2025, 1, 1));
        assertEquals(LocalDate.of(2025, 1, 1), item.getPurchasedDate());

        item.setStockLimit(2);
        assertEquals(2, item.getStockLimit());

    }

    @Test
    void testIsOutOfStock() {
        // Stock limit is 1, quantity = 10 -> not out of stock
        assertFalse(item.isOutOfStock());
        // Set quantity to 0 -> out of stock
        item.setQuantity(0);
        // Verify out of stock
        assertTrue(item.isOutOfStock());
    }

    @Test
    void testToString(){
        assertEquals(item.getItemName(), item.toString());
    }
}
