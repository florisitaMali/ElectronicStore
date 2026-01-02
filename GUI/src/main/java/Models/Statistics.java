package Models;

import DAO.*;

import java.time.LocalDate;
import java.util.ArrayList;

// Statistics depends on interfaces, not concrete DAOs
public class Statistics {

    private LocalDate date;
    private double totalItemCost;
    private double totalWagesCost;
    private double totalIncome;
    private double totalRevenue;
    private int nrOfBills;

    private static BillRepository billRepo = new BillDAOAdapter();
    private static ItemsRepository itemsRepo = new ItemsDAOAdapter();
    private static EmployeeRepository employeeRepo = new EmployeeDAOAdapter();

    public static void setBillRepository(BillRepository repo) {
        billRepo = repo;
    }

    public static void setItemsRepository(ItemsRepository repo) {
        itemsRepo = repo;
    }

    public static void setEmployeeRepository(EmployeeRepository repo) {
        employeeRepo = repo;
    }

    // ===== Constructor for production =====
    public Statistics(LocalDate date, double totalItemCost, double totalWagesCost,
                      double totalIncome, double totalRevenue, int nrOfBills) {
        this.date = date;
        this.totalItemCost = totalItemCost;
        this.totalWagesCost = totalWagesCost;
        this.totalIncome = totalIncome;
        this.totalRevenue = totalRevenue;
        this.nrOfBills = nrOfBills;

        // Use default adapters that wrap your static DAOs
        this.billRepo = new BillDAOAdapter();
        this.itemsRepo = new ItemsDAOAdapter();
        this.employeeRepo = new EmployeeDAOAdapter();
    }

    public Statistics(BillRepository billRepo,
                      ItemsRepository itemsRepo,
                      EmployeeRepository employeeRepo) {
        this.billRepo = billRepo;
        this.itemsRepo = itemsRepo;
        this.employeeRepo = employeeRepo;
    }

    // ===== GETTERS =====
    public LocalDate getDate() { return date; }
    public double getTotalItemCost() { return totalItemCost; }
    public double getTotalWagesCost() { return totalWagesCost; }
    public double getTotalIncome() { return totalIncome; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getNrOfBills() { return nrOfBills; }

    // ===== STATIC PURE METHOD =====
    public static double getTotalIncome(LocalDate startDate, LocalDate endDate, ArrayList<Bill> bills) {
        double totalIncome = 0;
        for (Bill b : bills) {
            totalIncome += b.getTotalPrice();
        }
        return totalIncome;
    }

    public static double getTotalCostOfPurchasingItem(LocalDate startDate, LocalDate endDate) {
        ArrayList<Bill> bills = billRepo.getAllBills(startDate, endDate);
        System.out.println("Before calculating total cost of purchasing items, number of bills: " + bills.size());
        double totalCost = 0;
        for (Bill b : bills) {
            totalCost += b.getBillsCost(b, startDate, endDate);
        }

        System.out.println("After calculating from bills, total cost: " + totalCost);
        for (Item i : itemsRepo.getAllItems()) {
            if (!i.getPurchasedDate().isBefore(startDate) && !i.getPurchasedDate().isAfter(endDate)) {
                totalCost += i.getPurchasedPrice() * i.getQuantity();
            }
        }
        System.out.println("After including unsold items, total cost: " + totalCost);
        return totalCost;
    }

    public static double getTotalCostOfSalary() {
        double totalCost = employeeRepo.getAdministrator().getSalary();

        for (Employee e : employeeRepo.getEmployees()) {
            totalCost += e.getSalary();
        }

        return totalCost;
    }
}
