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
    public Statistics(LocalDate date, double totalItemCost, double totalWagesCost, double totalIncome, double totalRevenue, int nrOfBills) {
        this.date = date;
        this.totalItemCost = totalItemCost;
        this.totalWagesCost = totalWagesCost;
        this.totalIncome = totalIncome;
        this.totalRevenue = totalRevenue;
        this.nrOfBills = nrOfBills;
        // Use default adapters that wrap your static DAOs
    }

    public Statistics(BillRepository bill, ItemsRepository items, EmployeeRepository employee) {
        billRepo = bill;
        itemsRepo = items;
        employeeRepo = employee;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTotalItemCost() {
        return totalItemCost;
    }

    public double getTotalWagesCost() {
        return totalWagesCost;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getNrOfBills() {
        return nrOfBills;
    }

    public static double getTotalIncome(LocalDate startDate, LocalDate endDate, ArrayList<Bill> bills) {
        if (bills == null || bills.isEmpty() || startDate == null || endDate == null) return 0;
        if (startDate.isAfter(endDate)) throw new IllegalArgumentException("Start date cannot be after end date.");

        double totalIncome = 0;
        for (Bill b : bills) {
            if (b.getSaleDate().toLocalDate().isBefore(startDate) || b.getSaleDate().toLocalDate().isAfter(endDate)) {
                continue;
            }
            totalIncome += b.getTotalPrice();
        }
        return totalIncome;
    }

    public static double getTotalCostOfPurchasingItem(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) throw new IllegalArgumentException("Start date and end date cannot be null.");
        if (startDate.isAfter(endDate)) throw new IllegalArgumentException("Start date cannot be after end date.");

        double totalCost = getTotalCostFromBills(startDate, endDate) + getTotalCostOfItem(startDate, endDate);

        System.out.println("After including unsold items, total cost: " + totalCost);
        return totalCost;
    }

    private static double getTotalCostFromBills(LocalDate startDate, LocalDate endDate) {
        double totalCost = 0;
        ArrayList<Bill> bills = billRepo.getAllBills(startDate, endDate);
        System.out.println("Before calculating total cost of purchasing items, number of bills: " + bills.size());
        for (Bill b : bills) {
            double c = b.getBillsCost(b, startDate, endDate);
            if (c > 0) {
                totalCost += c;
            }
        }
        return totalCost;
    }

    private static double getTotalCostOfItem(LocalDate startDate, LocalDate endDate) {
        double totalCost = 0;
        for (Item i : itemsRepo.getAllItems()) {
            if (!i.getPurchasedDate().isBefore(startDate) && !i.getPurchasedDate().isAfter(endDate)) {
                int q = i.getQuantity();
                double price = i.getPurchasedPrice();
                if (q > 0 && price > 0) {
                    totalCost += price * q;
                }
            }
        }
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
