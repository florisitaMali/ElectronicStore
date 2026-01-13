package Integration;

import FakeClasses.FakeCategory;
import FakeClasses.FakeItem;
import FakeClasses.FakeSupplier;
import Models.Category;
import Models.Item;
import Models.Supplier;
import Models.Sector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ItemCategorySupplierIntegrationTest {

    @Test
    void item_IsCorrectlyLinkedToCategoryAndSupplier() {
        // Bottom-Up Integration: model-level components
        FakeCategory category = new FakeCategory("FakeCategory", Sector.CAMERA);
        FakeItem item = new FakeItem("CameraItem", 5);

        // Simulate integration
        category.addFakeItem(item);

        // Act
        Category itemCategory = item.getItemCategory();
        Supplier itemSupplier = item.getItemSupplier();

        // Assert: item data
        assertEquals("CameraItem", item.getItemName());
        assertTrue(item.getQuantity() > 0);
        assertEquals(Sector.CAMERA, item.getSector());

        // Assert: category integration
        assertNotNull(itemCategory);
        assertEquals("FakeCategory", itemCategory.getName());
        assertNotNull(itemCategory.getItemsInThisCategory());

        // Assert: supplier integration (public API only)
        assertNotNull(itemSupplier);

        ArrayList<Item> supplierItems = itemSupplier.getProducts();
        assertNotNull(supplierItems);
        assertFalse(supplierItems.isEmpty());
    }
}
