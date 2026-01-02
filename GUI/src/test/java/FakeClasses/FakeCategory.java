package FakeClasses;

import Models.Category;
import Models.Item;
import Models.Sector;

import java.util.ArrayList;

public class FakeCategory extends Category {
    private ArrayList<Item> fakeItems = new ArrayList<>();

    public FakeCategory(String name, Sector sector) {
        super(name, sector);
    }

    public void addFakeItem(Item item) {
        fakeItems.add(item);
    }

    @Override
    public ArrayList<Item> getItemsInThisCategory() {
        return fakeItems;
    }
}
