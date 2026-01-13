package Integration;

import FakeClasses.FakeBillDAO;
import FakeClasses.FakeItemsDAO;
import FakeClasses.FakeEmployeeDAO;
import Models.Statistics;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsBillIntegrationTest {

    @Test
    void getTotalCostOfPurchasingItem_UsingFakeBillDAO() {
        Statistics.setBillRepository(new FakeBillDAO());
        Statistics.setItemsRepository(new FakeItemsDAO());
        Statistics.setEmployeeRepository(new FakeEmployeeDAO());

        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        double result = Statistics.getTotalCostOfPurchasingItem(start, end);

        // FakeBill returns cost = 60
        // FakeItemsDAO adds unsold items: (10 + 20)
        assertEquals(3060.0, result);
    }
}
