package Integration;

import FakeClasses.FakeBillDAO;
import FakeClasses.FakeItemsDAO;
import FakeClasses.FakeEmployeeDAO;
import Models.Employee;
import Models.Statistics;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsFullIntegrationTest {

    @Test
    void statistics_FullyIntegratedWithAllRepositories() {

        // Bottom-Up: inject already integrated repositories
        FakeBillDAO billRepo = new FakeBillDAO();
        FakeItemsDAO itemsRepo = new FakeItemsDAO();
        FakeEmployeeDAO employeeRepo = new FakeEmployeeDAO();

        Statistics.setBillRepository(billRepo);
        Statistics.setItemsRepository(itemsRepo);
        Statistics.setEmployeeRepository(employeeRepo);

        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        // Act: Statistics uses integrated repositories
        double purchasingCost =
                Statistics.getTotalCostOfPurchasingItem(start, end);

        double salaryCost =
                Statistics.getTotalCostOfSalary();

        // Expected salary (from integrated fake behavior)
        double expectedSalary =
                employeeRepo.getAdministrator().getSalary()
                        + employeeRepo.getEmployees()
                        .stream()
                        .mapToDouble(Employee::getSalary)
                        .sum();

        // Assert: full integration
        assertTrue(purchasingCost > 0);
        assertEquals(expectedSalary, salaryCost);
    }
}
