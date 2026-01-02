package FakeClasses;

import Models.Employee;
import Models.Permission;
import Models.Role;
import Models.NotValidUsername;

import java.time.LocalDate;
import java.util.ArrayList;

// A Fake Employee for testing other classes without DB or real Employee logic
public class FakeEmployee extends Employee {

    public FakeEmployee(Role role) {
        super(role);
    }

    @Override
    public ArrayList<Permission> getAccessLevel() {
        ArrayList<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.ADD_ITEMS_TO_STOCK);
        permissions.add(Permission.MANAGE_EMPLOYEES);
        return permissions;
    }

    @Override
    public double getSalary() {
        return 1000;
    }
}
