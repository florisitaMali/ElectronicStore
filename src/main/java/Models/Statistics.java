package Models;

import DAO.BillDAO;
import DAO.EmployeeDAO;
import DAO.ItemsDAO;

import java.time.LocalDate;
import java.util.ArrayList;

public class Statistics {
    private LocalDate date;
    private double totalItemCost;
    private double totalWagesCost;
    private double totalIncome;
    private double totalRevenue;
    private int nrOfBills;

    public Statistics(LocalDate date, double totalItemCost, double totalWagesCost, double totalIncome, double totalRevenue, int nrOfBills)
    {
        this.date = date;
        this.totalItemCost = totalItemCost;
        this.totalWagesCost = totalWagesCost;
        this.totalIncome = totalIncome;
        this.totalRevenue = totalRevenue;
        this.nrOfBills = nrOfBills;
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
        double totalIncome = 0;
        //The incomes come only from bills
        for (Bill b : bills) {
            //Each bill has stored the totalIncome
            totalIncome += b.getTotalPrice();
        }
        return totalIncome;
    }

    public static double getTotalCostOfPurchasingItem(LocalDate startDate, LocalDate endDate) {
        ArrayList<Bill> bills = BillDAO.getAllBills(startDate, endDate);

        //The cost from the items sold
        double totalCost = 0;
        for (Bill b : bills) {
            totalCost += Bill.getBillsCost(b, startDate, endDate);
        }

        //The cost from the items not sold yet
        for (Item i : ItemsDAO.getAllItems()) {
            if ((i.getPurchasedDate().isAfter(startDate) || i.getPurchasedDate().isEqual(startDate)) && (i.getPurchasedDate().isBefore(endDate) || i.getPurchasedDate().isEqual(endDate))) {
                totalCost += (i.getPurchasedPrice() * i.getQuantity());
            }
        }
        return totalCost;
    }

    public static double getTotalCostOfSalary() {
        double totalCost = 0;
        //Include the cost of Administrator
        totalCost += EmployeeDAO.getAdministrator().getSalary();

        //Include the cost of employees
        ArrayList<Employee> employees = EmployeeDAO.getEmployees();
        for (Employee e : employees) {
            totalCost += e.getSalary();
        }

        return totalCost;
    }
}
