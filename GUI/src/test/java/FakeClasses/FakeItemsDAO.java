package FakeClasses;

import Models.Item;
import DAO.ItemsRepository;
import FakeClasses.FakeItem;
import FakeClasses.FakeSoldItem;
import Models.ItemNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FakeItemsDAO implements ItemsRepository {

    private final ArrayList<Item> items;

    public FakeItemsDAO() {
        items = new ArrayList<>();
        items.add(new FakeItem("Item1", 10));
        items.add(new FakeItem("Item2", 20));
    }

    @Override
    public ArrayList<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public void addItem(FakeItem item) {
        items.add(item);
    }

    public void deleteItem(FakeItem item) {
        items.remove(item);
    }

    public Item searchItem(String name) throws ItemNotFoundException {
        for (Item item : items) {
            if (item.getItemName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        throw  new ItemNotFoundException("Item not found: " + name);
    }

    public Map<String, Integer> getItemsPurchasedStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> stats = new HashMap<>();
        for (Item item : items) {
            stats.put(item.getItemName(), item.getQuantity());
        }
        return stats;
    }

    public int getItemId(FakeSoldItem item) {
        return items.indexOf(item) + 1; // simple ID for testing
    }

    public void updateItem(Item item) {
        // For testing, assume item is updated in the list
    }

    public void softDeleteItem(FakeItem item) {
        item.setDeleted(true);
    }
}
