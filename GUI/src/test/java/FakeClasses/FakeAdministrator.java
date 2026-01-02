package FakeClasses;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import Models.Administrator;
import Models.Employee;
import Models.Permission;
import Models.Role;

public class FakeAdministrator extends Administrator {
    private static double totalIncome = 0;
    private static double totalCost = 0;
    private final Set<Permission> permissions = new HashSet<>();

    public FakeAdministrator() {
        addPermissions();
    }

    public FakeAdministrator(String name, String surname, String username,
                             String psw, String email, String phoneNr,
                             LocalDate dateOfBirth, double salary) {
        addPermissions();
    }

    private void addPermissions() {
        permissions.add(Permission.GENERATE_PRINTABLE_BILL);
        permissions.add(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY);
        permissions.add(Permission.ADD_ITEMS_TO_STOCK);
        permissions.add(Permission.GENERATE_TOTAL_COST_INCOME);
        permissions.add(Permission.MANAGE_EMPLOYEES);
        permissions.add(Permission.ACCESS_STATISTICS_ABOUT_SOLD_AND_PURCHASED_ITEMS);
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public static double getTotalIncome() { return totalIncome; }
    public static void setTotalIncome(double totalIncome) { FakeAdministrator.totalIncome = totalIncome; }

    public static double getTotalCost() { return totalCost; }
    public static void setTotalCost(double totalCost) { FakeAdministrator.totalCost = totalCost; }

    @Override
    public String toString() {
        return super.toString() + " Total Cost: " + totalCost;
    }
}
