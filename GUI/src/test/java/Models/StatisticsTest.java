package Models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import DAO.EmployeeDAO;
import FakeClasses.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StatisticsTest {
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.now();

        // Swap in fakes for testing
        Statistics.setBillRepository(new FakeBillDAO());
        Statistics.setItemsRepository(new FakeItemsDAO());
        Statistics.setEmployeeRepository(new FakeEmployeeDAO());

        try {
            Administrator admin = new Administrator("Admin", "One", "admin", "pass", "a@a.com", "111", LocalDate.of(1990, 1, 1), 5000);
            EmployeeDAO.addEmployee(admin);
        } catch (Exception e) {

        }
    }

    @Test
    void constructor_shouldInitializeAllFieldsCorrectly() {
        Statistics stats = new Statistics(startDate, 1000.0, 2000.0, 5000.0, 2000.0, 10);

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


    @ParameterizedTest
    @MethodSource("provideBillsForTotalIncome")
    void testGetTotalIncome(LocalDate startDate, LocalDate endDate, ArrayList<Bill> bills, double expectedTotal) {
        double result = Statistics.getTotalIncome(startDate, endDate, bills);
        assertEquals(expectedTotal, result, 0.001);
    }

    static Stream<Arguments> provideBillsForTotalIncome() {
        Bill bill1 = new FakeBill() {
            @Override
            public LocalDateTime getSaleDate() {
                return LocalDateTime.of(2026, 1, 15, 10, 0);
            }

            @Override
            public double getTotalPrice() {
                return 200.0;
            }
        };
        Bill bill2 = new FakeBill() {
            @Override
            public LocalDateTime getSaleDate() {
                return LocalDateTime.of(2026, 1, 20, 10, 0);
            }

            @Override
            public double getTotalPrice() {
                return 300.0;
            }
        };
        Bill bill3 = new FakeBill() {
            @Override
            public LocalDateTime getSaleDate() {
                return LocalDateTime.of(2026, 1, 25, 10, 0);
            }

            @Override
            public double getTotalPrice() {
                return 100.0;
            }
        };

        ArrayList<Bill> emptyList = new ArrayList<>();
        ArrayList<Bill> allBills = new ArrayList<>(List.of(bill1, bill2, bill3));

        return Stream.of(
                //null bills
                Arguments.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, 0.0),
                //empty bills
                Arguments.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), emptyList, 0.0),
                //startDate null
                Arguments.of(null, LocalDate.of(2026, 1, 31), allBills, 0.0),
                //endDate null
                Arguments.of(LocalDate.of(2026, 1, 1), null, allBills, 0.0),
                //no bills in date range
                Arguments.of(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), allBills, 0.0),
                //some bills in date range
                Arguments.of(LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 18), allBills, 200.0),
                //all bills in date range
                Arguments.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), allBills, 600.0));
    }

    @Test
    void testStartDateAfterEndDateThrows() {
        ArrayList<Bill> bills = new ArrayList<>();
        bills.add(new Bill());

        assertThrows(IllegalArgumentException.class, () -> {
            Statistics.getTotalIncome(LocalDate.of(2026, 1, 31), LocalDate.of(2026, 1, 1), bills);
        });
    }

    @ParameterizedTest
    @MethodSource("provideDatesAndExpectedTotalCost")
    void testGetTotalCostOfPurchasingItem(LocalDate startDate, LocalDate endDate, List<Bill> bills, List<Item> items, double expectedTotal) {

        FakeBillDAO fakeBillDAO = new FakeBillDAO() {
            @Override
            public ArrayList<Bill> getAllBills(LocalDate start, LocalDate end) {
                return new ArrayList<>(bills);
            }
        };
        Statistics.setBillRepository(fakeBillDAO);

        FakeItemsDAO fakeItemsDAO = new FakeItemsDAO() {
            @Override
            public ArrayList<Item> getAllItems() {
                return new ArrayList<>(items);
            }
        };
        Statistics.setItemsRepository(fakeItemsDAO);

        if (startDate == null || endDate == null || (startDate != null && endDate != null && startDate.isAfter(endDate))) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                Statistics.getTotalCostOfPurchasingItem(startDate, endDate);
            });
        } else {
            double total = Statistics.getTotalCostOfPurchasingItem(startDate, endDate);
            Assertions.assertEquals(expectedTotal, total, 0.001);
        }
    }

    static Stream<Arguments> provideDatesAndExpectedTotalCost() {
        Bill bill1 = new FakeBill() {
            @Override
            public LocalDateTime getSaleDate() {
                return LocalDateTime.of(2025, 2, 15, 10, 0);
            }

            @Override
            public double getBillsCost(Bill bill, LocalDate startDate, LocalDate endDate) {
                return 200.0;
            }
        };
        Bill bill2 = new FakeBill() {
            @Override
            public LocalDateTime getSaleDate() {
                return LocalDateTime.of(2025, 2, 20, 10, 0);
            }

            @Override
            public double getBillsCost(Bill bill, LocalDate startDate, LocalDate endDate) {
                return 200.0;
            }
        };
        Bill bill3 = new FakeBill() {
            @Override
            public LocalDateTime getSaleDate() {
                return LocalDateTime.of(2025, 2, 15, 10, 0);
            }

            @Override
            public double getBillsCost(Bill bill, LocalDate startDate, LocalDate endDate) {
                return 0;
            }
        };

        Item item1 = new FakeItem("Item1", 0) {
            @Override
            public double getPurchasedPrice() {
                return 0.0;
            }

            @Override
            public LocalDate getPurchasedDate() {
                return LocalDate.of(2025, 2, 18);
            }

        };    // quantity 0, price 0
        Item item2 = new FakeItem("Item2", 1) {
            @Override
            public double getPurchasedPrice() {
                return 0.0;
            }

            @Override
            public LocalDate getPurchasedDate() {
                return LocalDate.of(2025, 2, 22);
            }

        };
        Item item3 = new FakeItem("Item3", 1) {
            @Override
            public double getPurchasedPrice() {
                return 200.0;
            }

            @Override
            public LocalDate getPurchasedDate() {
                return LocalDate.of(2025, 2, 25);
            }
        };
        ArrayList<Bill> emptyBills = new ArrayList<>();
        ArrayList<Item> emptyItems = new ArrayList<>();

        return Stream.of(
                //TC1: startDate null
                Arguments.of(null, LocalDate.of(2025, 3, 1), null, null, 0.0),
                //TC2: endDate null
                Arguments.of(LocalDate.of(2025, 1, 12), null, null, null, 0.0),
                //TC3: startDate after endDate
                Arguments.of(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 1, 12), null, null, 0.0),
                //TC4: empty bills, empty items
                Arguments.of(LocalDate.of(2025, 2, 12), LocalDate.of(2025, 3, 12), emptyBills, emptyItems, 0.0),
                //TC5: some bills, empty items, cost=0
                Arguments.of(LocalDate.of(2025, 2, 12), LocalDate.of(2025, 3, 12), List.of(bill3), emptyItems, 0.0),
                //TC6: some bills with cost 200, empty items
                Arguments.of(LocalDate.of(2025, 2, 12), LocalDate.of(2025, 3, 12), List.of(bill1), emptyItems, 200.0),
                //TC7: some bills, range check
                Arguments.of(LocalDate.of(2025, 2, 12), LocalDate.of(2025, 3, 12), List.of(bill1, bill2), emptyItems, 400.0),
                //TC8: empty bills, some items, cost 0
                Arguments.of(LocalDate.of(2025, 2, 12), LocalDate.of(2025, 3, 12), emptyBills, List.of(item1), 0.0),
                //TC9: empty bills, some items with quantity=1, price=0
                Arguments.of(LocalDate.of(2025, 2, 12), LocalDate.of(2025, 3, 12), emptyBills, List.of(item2), 0.0),
                //TC10: empty bills, some items quantity=1, price=200
                Arguments.of(LocalDate.of(2025, 2, 12), LocalDate.of(2025, 3, 12), emptyBills, List.of(item3), 200.0),
                //TC11: some bills and some items, total cost
                Arguments.of(LocalDate.of(2025, 2, 12), LocalDate.of(2025, 3, 12), List.of(bill1), List.of(item3), 400.0));
    }

    @Test
    void getTotalCostOfSalary_shouldIncludeAllEmployees() {
        // Use the fake classes to avoid using the real DAOs
        Statistics stats = new Statistics(new FakeBillDAO(), new FakeItemsDAO(), new FakeEmployeeDAO());
        //The FakeEmployeeDAO returns four employees with salaries 1000.0 each, 2 are Cashiers, 1 Manager, 1 Administrator
        double totalSalaryCost = stats.getTotalCostOfSalary();
        assertEquals(9000, totalSalaryCost);
    }

    @Test
    void getTotalCostOfSalary_shouldReturnZeroForEmptyEmployeeDAO() {
        Statistics.setEmployeeRepository(new FakeEmployeeDAO() {
            @Override
            public ArrayList<Employee> getEmployees() {
                return new ArrayList<>();
            }

            @Override
            public Employee getAdministrator(){
                return new Administrator();
            }
        });

        //Total salary should be zero
        double totalSalary = Statistics.getTotalCostOfSalary();
        assertEquals(0.0, totalSalary, "Total salary should be zero when no employees exist");
        // Just for safety
        assertTrue(totalSalary >= 0, "Total salary must not be negative");
    }
}
