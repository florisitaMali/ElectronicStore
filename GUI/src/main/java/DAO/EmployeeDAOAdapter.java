package DAO;

import Models.Administrator;
import Models.Employee;

import java.util.ArrayList;

public class EmployeeDAOAdapter implements EmployeeRepository {
    @Override
    public Employee getAdministrator() {
        return EmployeeDAO.getAdministrator();
    }

    @Override
    public ArrayList<Employee> getEmployees() {
        return EmployeeDAO.getEmployees(new Administrator());
    }
}