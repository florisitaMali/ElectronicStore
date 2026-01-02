package Models;

import FakeClasses.FakeBill;
import FakeClasses.FakeSoldItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BillTest {

    private FakeBill bill;

    @BeforeEach
    void setUp() {
        bill = new FakeBill();
    }

    @Test
    void getTotalPrice_shouldReturnFakeValue() {
        assertEquals(100.0, bill.getTotalPrice());
    }

    @Test
    void getBillNumber_shouldReturnFakeValue() {
        assertEquals(111111, bill.getBillNumber());
    }

    @Test
    void getSaleDate_shouldReturnFixedDate() {
        assertEquals("2025-06-01T12:00", bill.getSaleDate().toString());
    }

    @Test
    void getEmployee_shouldReturnCashier() {
        assertEquals(Role.CASHIER, bill.getEmployee().getRole());
    }

    @Test
    void getSoldItems_shouldReturnFakeItems() {
        ArrayList<SoldItem> items = bill.getSoldItems();
        assertEquals(2, items.size());
        assertEquals("I1", items.get(0).getItemName());
        assertEquals(2, items.get(0).getSoldQuantity());
        assertEquals("I2", items.get(1).getItemName());
        assertEquals(3, items.get(1).getSoldQuantity());
    }

    @Test
    void getBillsCost_shouldReturnFakeValue() {
        double cost = bill.getBillsCost(bill, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertEquals(60.0, cost);
    }

    @Test
    void getBills_shouldReturnListWithOneFakeBill() {
        ArrayList<Bill> bills = bill.getBills(bill.getEmployee(), LocalDate.now(), LocalDate.now());
        assertEquals(1, bills.size());
        assertTrue(bills.get(0) instanceof FakeBill);
    }

    @Test
    void addSoldItems_shouldDoNothing() {
        bill.addSoldItems(new FakeSoldItem("X", 1, 10));
        // No exception, no change expected
        assertEquals(2, bill.getSoldItems().size()); // still returns fake items
    }

    @Test
    void deleteSoldItem_shouldDoNothing() {
        bill.deleteSoldItem(new FakeSoldItem("I1", 2, 30));
        // No exception, still returns fake items
        assertEquals(2, bill.getSoldItems().size());
    }
}
