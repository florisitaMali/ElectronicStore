package FakeClasses;

import Models.Item;
import Models.Supplier;

import java.util.ArrayList;

public class FakeSupplier extends Supplier {
    public FakeSupplier(String supplierName, String address) {
        super(supplierName, address);
    }

    public FakeSupplier(String supplierName) {
        super(supplierName);
    }

    public void addFakeItem(Item item) {
        //Do nothing for testing
    }

    @Override
    public ArrayList<Item> getProducts() {
        ArrayList<Item> fakeItems = new ArrayList<>();
        fakeItems.add(new FakeItem("FakeItem1", 5));
        fakeItems.add(new FakeItem("FakeItem2", 10));
        return fakeItems;
    }
}
