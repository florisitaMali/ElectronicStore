package DAO;

import Models.Employee;

import java.util.ArrayList;

public interface EmployeeRepository {
    Employee getAdministrator();
    ArrayList<Employee> getEmployees();
}
