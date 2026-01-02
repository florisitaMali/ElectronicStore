package FakeClasses;

import Models.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class FakeStatistics extends Statistics{

    public FakeStatistics(){
        super(LocalDate.now(), 600, 200, 1000, 200, 5);
    }

    public static double getTotalIncome(LocalDate startDate, LocalDate endDate, ArrayList<Bill> bills) {
        return 1000;
    }

    public static double getTotalCostOfPurchasingItem(LocalDate startDate, LocalDate endDate, ArrayList<Bill> bills, ArrayList<Item> items) {
        return 600;
    }

    public static double getTotalCostOfSalary(Employee administrator, ArrayList<Employee> employees) {
        return 200;
    }
}
