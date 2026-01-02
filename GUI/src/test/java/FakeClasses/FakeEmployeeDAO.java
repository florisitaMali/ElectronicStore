package FakeClasses;

import DAO.EmployeeDAO;
import DAO.EmployeeRepository;
import Models.*;

import java.util.ArrayList;
import java.util.List;

public class FakeEmployeeDAO implements EmployeeRepository {

    @Override
    public Employee getAdministrator() {
        return new FakeAdministrator();
    }

    @Override
    public ArrayList<Employee> getEmployees() {
        ArrayList<Employee> list = new ArrayList<>();
        list.add(new FakeEmployee(Role.CASHIER));
        list.add(new FakeEmployee(Role.CASHIER));
        list.add(new FakeEmployee(Role.MANAGER));
        list.add(new FakeEmployee(Role.ADMINISTRATOR));
        return list;
    }
}
