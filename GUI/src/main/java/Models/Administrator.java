package Models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import DAO.EmployeeDAO;
import DAO.BillDAO;
import DAO.ItemsDAO;

public class Administrator extends Employee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static ArrayList<Employee> employees;
    private static double totalIncome = 0;
    private static double totalCost = 0;

    public Administrator(){
        super(Role.ADMINISTRATOR);
        addPermissions();;
    }

    public Administrator(String name, String surname, String username, String psw, String email, String phoneNr, LocalDate dateOfBirth, double salary)
    {
        super(name, surname, username, psw, email, phoneNr,dateOfBirth, salary, Role.ADMINISTRATOR);
        addPermissions();
    }

    private void addPermissions()
    {
        addPermission(Permission.GENERATE_PRINTABLE_BILL);
        addPermission(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY);
        addPermission(Permission.ADD_ITEMS_TO_STOCK);
        addPermission(Permission.GENERATE_TOTAL_COST_INCOME);
        addPermission(Permission.MANAGE_EMPLOYEES);
        addPermission(Permission.ACCESS_STATISTICS_ABOUT_SOLD_AND_PURCHASED_ITEMS);
    }

    @Override
    public String toString() {
        return super.toString() + " Total Cost: " + totalCost;
    }
}

