package Models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import DAO.BillDAO;
import DAO.EmployeeDAO;
import FakeClasses.FakeBill;
import FakeClasses.FakeBillDAO;
import FakeClasses.FakeEmployeeDAO;
import FakeClasses.FakeItemsDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatisticsTest {
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2024, 1, 1);
        endDate   = LocalDate.now();

        // Swap in fakes for testing
        Statistics.setBillRepository(new FakeBillDAO());
        Statistics.setItemsRepository(new FakeItemsDAO());
        Statistics.setEmployeeRepository(new FakeEmployeeDAO());
    }

    @Test
    void constructor_shouldInitializeAllFieldsCorrectly() {
        Statistics stats = new Statistics(
                startDate,
                1000.0,
                2000.0,
                5000.0,
                2000.0,
                10
        );

        assertEquals(startDate, stats.getDate());
        assertEquals(1000.0, stats.getTotalItemCost());
        assertEquals(2000.0, stats.getTotalWagesCost());
        assertEquals(5000.0, stats.getTotalIncome());
        assertEquals(2000.0, stats.getTotalRevenue());
        assertEquals(10, stats.getNrOfBills());
    }

    @Test
    void getTotalIncome_shouldSumAllBillTotals() {
        ArrayList<Bill> bills = new ArrayList<>();

        bills.add(new FakeBill()); //First Bill
        bills.add(new FakeBill()); //Second Bill

        //Calculate total income from the two fake bills
        double totalIncome = Statistics.getTotalIncome(startDate, endDate, bills);

        //Expected is twice the total price of one fake bill since there are two identical bills
        //Compare with actual
        assertEquals(new FakeBill().getTotalPrice() * 2, totalIncome);
    }

    @Test
    void getTotalIncome_shouldReturnZeroForEmptyBillList() {
        //Empty list
        ArrayList<Bill> bills = new ArrayList<>();

        //Must return zero
        double totalIncome = Statistics.getTotalIncome(startDate, endDate, bills);

        //Compare with actual
        assertEquals(0.0, totalIncome);
    }

    @Test
    void getTotalCostOfPurchasingItem_shouldIncludeSoldAndUnsoldItems() {
        // Use the fake classes to avoid using the real DAOs
        Statistics stats = new Statistics(new FakeBillDAO(), new FakeItemsDAO(), new FakeEmployeeDAO());

        /// The cost of Sold Items
        //FakeBillDAO returns one bill
        //Each FakeBill returns 60.0 as the cost of purchasing items -> total should be 60.0

        /// The cost of Unsold Items
        //FakeItemsDAO returns two items with quantity 10 and 20
        //Each item has a cost price of 100.0 -> total should be (10*100) + (20*100) = 3000.0

        //Total cost = 60.0 + 3000.0 = 3060.0
        double totalCost = stats.getTotalCostOfPurchasingItem(startDate, endDate);
        assertEquals(3060, totalCost);
    }

    @Test
    void getTotalCostOfPurchasingItem_shouldReturnZeroForEmptyDAOs() {
        // Swap in empty fakes
        Statistics.setBillRepository(new FakeBillDAO(){
            @Override
            public ArrayList<Bill> getAllBills(LocalDate start, LocalDate end) {
                return new ArrayList<>(); // Return empty list
            }
        });
        // Swap in empty items DAO
        Statistics.setItemsRepository(new FakeItemsDAO(){
            @Override
            public ArrayList<Item> getAllItems() {
                return new ArrayList<>(); // Return empty list
            }
        });
        Statistics.setEmployeeRepository(new FakeEmployeeDAO());

        //Total cost should be zero
        double totalCost = Statistics.getTotalCostOfPurchasingItem(startDate, endDate);
        // Verify
        assertEquals(0.0, totalCost, "Total cost should be zero when no bills or items exist");
        // Just for safety
        assertTrue(totalCost >= 0, "Total cost must not be negative");
    }

    @Test
    void getTotalCostOfSalary_shouldIncludeAllEmployees() {
        // Use the fake classes to avoid using the real DAOs
        Statistics stats = new Statistics(new FakeBillDAO(), new FakeItemsDAO(), new FakeEmployeeDAO());
        //The FakeEmployeeDAO returns four employees with salaries 1000.0 each, 2 are Cashiers, 1 Manager, 1 Administrator
        double totalSalaryCost = stats.getTotalCostOfSalary();
        assertEquals(4000, totalSalaryCost);
    }

    @Test
    void getTotalCostOfSalary_shouldReturnZeroForEmptyEmployeeDAO() {
        // Swap in empty employee DAO
        Statistics.setEmployeeRepository(new FakeEmployeeDAO(){
            @Override
            public ArrayList<Employee> getEmployees() {
                return new ArrayList<>(); // Return empty list
            }
        });

        //Total salary should be zero
        double totalSalary = Statistics.getTotalCostOfSalary();
        // Verify
        assertEquals(0.0, totalSalary, "Total salary should be zero when no employees exist");
        // Just for safety
        assertTrue(totalSalary >= 0, "Total salary must not be negative");
    }
}
