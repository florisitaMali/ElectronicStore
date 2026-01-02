package FakeClasses;

import Models.Bill;
import Models.SoldItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DAO.BillRepository;

// Implements the BillRepository interface for testing
public class FakeBillDAO implements BillRepository {

    @Override
    public ArrayList<Bill> getAllBills(LocalDate start, LocalDate end) {
        ArrayList<Bill> bills = new ArrayList<>();
        bills.add(new FakeBill()); // returns a fake bill with fake sold items
        return bills;
    }

    // Optional: other fake methods for testing statistics
    public void saveBill(Bill bill) {
        // do nothing in the fake
    }

    public Map<String, Integer> getItemsSoldStatistics(LocalDate start, LocalDate end) {
        Map<String, Integer> stats = new HashMap<>();
        List<SoldItem> items = new FakeBill().getSoldItems();
        for (SoldItem item : items) {
            stats.put(item.getItemName(), item.getQuantity());
        }
        return stats;
    }
}
