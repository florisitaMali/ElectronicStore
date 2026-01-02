package FakeClasses;

import Models.*;
import FakeClasses.FakeSoldItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FakeBill extends Bill {
    public FakeBill() {
        super();
    }

    @Override
    public double getTotalPrice() {
        return 100.0;
    }

    @Override
    public long getBillNumber() {
        return 111111;
    }

    @Override
    public LocalDateTime getSaleDate() {
        return LocalDateTime.of(2025, 6, 1, 12, 0);
    }

    @Override
    public Employee getEmployee() {
        return new Cashier();
    }

    @Override
    public ArrayList<SoldItem> getSoldItems() {
        ArrayList<SoldItem> items = new ArrayList<>();
        items.add(new FakeSoldItem("I1", 2, 30));
        items.add(new FakeSoldItem("I2", 3, 40));
        return items;
    }

    @Override
    public double getBillsCost(Bill b, LocalDate startDate, LocalDate endDate) {
        return 60.0;
    }

    @Override
    public ArrayList<Bill> getBills(Employee emp, LocalDate start, LocalDate end) {
        ArrayList<Bill> bills = new ArrayList<>();
        bills.add(new FakeBill());
        return bills;
    }

    @Override
    public void addSoldItems(SoldItem s) {
        // Do nothing for the fake
    }

    @Override
    public void deleteSoldItem(SoldItem s) {
        // Do nothing for the fake
    }
}
