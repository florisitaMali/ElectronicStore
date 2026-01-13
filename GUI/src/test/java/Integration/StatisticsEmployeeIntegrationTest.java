package Integration;

import FakeClasses.FakeEmployeeDAO;
import Models.Statistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsEmployeeIntegrationTest {

    @Test
    void getTotalCostOfSalary_WithFakeEmployeeDAO() {
        Statistics.setEmployeeRepository(new FakeEmployeeDAO());

        double totalSalary = Statistics.getTotalCostOfSalary();

        assertTrue(totalSalary > 0);
    }
}
