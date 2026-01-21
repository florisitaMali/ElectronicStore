package Integration;

import FakeClasses.FakeBill;
import FakeClasses.FakeBillDAO;
import Models.Bill;
import Models.SoldItem;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BillSoldItemIntegrationTest {

    @Test
    void getBills_ReturnBillsWithSoldItems() {
        FakeBillDAO fakeBillDAO = new FakeBillDAO();

        ArrayList<Bill> bills = fakeBillDAO.getAllBills(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        assertNotNull(bills);
        assertEquals(1, bills.size());

        Bill bill = bills.get(0);

        assertNotNull(bill);
        assertEquals(111111, bill.getBillNumber());

        ArrayList<SoldItem> soldItems = bill.getSoldItems();
        assertNotNull(soldItems);
        assertEquals(2, soldItems.size());

        for (SoldItem item : soldItems) {
            assertNotNull(item.getItemName());
            assertTrue(item.getSoldQuantity() > 0);
            assertTrue(item.getSellingPrice() > 0);
        }
    }
}
