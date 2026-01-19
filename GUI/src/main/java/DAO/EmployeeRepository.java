package DAO;

import Models.Employee;
import Models.Role;

import java.util.ArrayList;

public interface EmployeeRepository {
    Employee getAdministrator();
    ArrayList<Employee> getEmployees();
    Employee searchEmployee(String username, Role role);
}
