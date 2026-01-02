package Models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import FakeClasses.FakeItemsDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupplierTest {
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = new Supplier("ACME Corp", "Main Street 1");
    }

    @Test
    void setSupplierName_shouldUpdatePropertyValue() {
        // Update supplier name
        supplier.setSupplierName("New Supplier");

        // Verify the update
        assertEquals("New Supplier", supplier.getSupplierName());
    }

    @Test
    void setAddress_shouldUpdatePropertyValue() {
        //Update address
        supplier.setAddress("New Address");

        //Verify the update
        assertEquals("New Address", supplier.getAddress());
    }

    @Test
    void supplierNameProperty_shouldNotBeNull() {
        // Verify that the supplierNameProperty is not null
        assertNotNull(supplier.supplierNameProperty());
    }

    @Test
    void addressProperty_shouldNotBeNull() {
        // Verify that the addressProperty is not null
        assertNotNull(supplier.addressProperty());
    }

    @Test
    void getProducts_shouldReturnOnlyItemsFromThisSupplier() {
        //All of the Items from the ItemsDAO should have this supplier as their new Supplier("FakeSupplier")
        FakeItemsDAO fakeItemsDAO = new FakeItemsDAO();
        Supplier.setItemsDAO(fakeItemsDAO);
        //Set supplier name to match the fake items' supplier
        supplier.setSupplierName("FakeSupplier");
        //It must return 2 items from the FakeItemsDAO
        ArrayList<Item> products = supplier.getProducts();

        // Check if the products list contains only items from this supplier
        assertEquals(2, products.size());
        // Verify that each item's supplier matches this supplier
        for (Item item : products) {
            assertEquals(
                    supplier.getSupplierName(),
                    item.getItemSupplier().getSupplierName()
            );
        }
    }

    @Test
    void getProducts_shouldReturnEmptyListIfNoItemsMatchSupplier() {
        //Set supplier name that does not match any item
        Supplier unknownSupplier = new Supplier("NonExistingSupplier");
        // Use the FakeItemsDAO
        Supplier.setItemsDAO(new FakeItemsDAO());

        //Get the products which should be empty
        ArrayList<Item> products = unknownSupplier.getProducts();

        //The list must not be null but empty
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void toString_shouldReturnSupplierName() {
        supplier.setSupplierName("Cagatay");
        assertEquals("Cagatay", supplier.toString());
    }
}
