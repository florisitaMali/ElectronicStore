package FakeClasses;

import DAO.EmployeeDAO;
import DAO.EmployeeRepository;
import Models.*;

import java.util.ArrayList;
import java.util.List;

public class FakeEmployeeDAO implements EmployeeRepository {

    private List<Employee> employees;

    @Override
    public Employee getAdministrator() {
        return EmployeeDAO.getAdministrator();
    }

    public void addEmployee(Employee emp) {
        employees.add(emp);
    }

    @Override
    public Employee searchEmployee(String username, Role role) {
        Employee emp = getAdministrator();
        return emp;
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
