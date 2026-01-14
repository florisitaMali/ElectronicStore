package Integration;

import FakeClasses.FakeItemsDAO;
import FakeClasses.FakeBillDAO;
import FakeClasses.FakeEmployeeDAO;
import Models.Statistics;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsItemsIntegrationTest {

    @Test
    void getTotalCostOfPurchasingItem_IncludesUnsoldItems() {
        Statistics.setBillRepository(new FakeBillDAO());
        Statistics.setItemsRepository(new FakeItemsDAO());

        double cost = Statistics.getTotalCostOfPurchasingItem(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        assertTrue(cost > 0);
    }
}
