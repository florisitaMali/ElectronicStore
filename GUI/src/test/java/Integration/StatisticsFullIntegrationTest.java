package Integration;

import Models.*;
import DAO.*;

import FakeClasses.FakeBillDAO;
import FakeClasses.FakeItemsDAO;
import FakeClasses.FakeEmployeeDAO;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsFullIntegrationTest {

    private static BillRepository billRepo;
    private static ItemsRepository itemsRepo;
    private static EmployeeRepository employeeRepo;

    private static LocalDate startDate;
    private static LocalDate endDate;

    @BeforeAll
    public static void setupIntegration() {

        // Bottom-Up: concrete implementations of repository interfaces
        billRepo = new FakeBillDAO();
        itemsRepo = new FakeItemsDAO();
        employeeRepo = new FakeEmployeeDAO();

        // Inject repositories into Statistics
        Statistics.setBillRepository(billRepo);
        Statistics.setItemsRepository(itemsRepo);
        Statistics.setEmployeeRepository(employeeRepo);

        startDate = LocalDate.of(2025, 1, 1);
        endDate   = LocalDate.of(2025, 12, 31);
    }

    @Test
    public void testGetTotalIncomeWithinDateRange() {

        ArrayList<Bill> bills = billRepo.getAllBills(startDate, endDate);

        double income = Statistics.getTotalIncome(startDate, endDate, bills);

        assertTrue(income > 0);
    }

    @Test
    public void testGetTotalIncomeWithEmptyBills() {

        double income = Statistics.getTotalIncome(startDate, endDate, new ArrayList<>());

        assertEquals(0, income);
    }

    // getTotalCostOfPurchasingItem
    @Test
    public void testGetTotalCostOfPurchasingItem() {

        double cost = Statistics.getTotalCostOfPurchasingItem(startDate, endDate);

        assertTrue(cost > 0);
    }

    @Test
    public void testGetTotalCostOfPurchasingItemInvalidDates() {

        assertThrows(IllegalArgumentException.class, () ->
                Statistics.getTotalCostOfPurchasingItem(endDate, startDate));
    }

    @Test
    public void testGetTotalCostOfSalary() {

        double calculatedSalary = Statistics.getTotalCostOfSalary();

        double expectedSalary =
                employeeRepo.getAdministrator().getSalary()
                        + employeeRepo.getEmployees()
                        .stream()
                        .mapToDouble(Employee::getSalary)
                        .sum();

        assertEquals(expectedSalary, calculatedSalary);
    }

    @Test
    public void testStatisticsConstructorWithRepositories() {

        Statistics statistics = new Statistics(
                billRepo,
                itemsRepo,
                employeeRepo
        );

        double salaryCost = Statistics.getTotalCostOfSalary();

        assertTrue(salaryCost > 0);
    }
}
