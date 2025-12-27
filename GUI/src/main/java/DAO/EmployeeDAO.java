package DAO;

import Models.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeeDAO {
    public static final File EMPLOYEE_FILE = new File("src/main/resources/com/example/gui/Employee.dat");

    public static ArrayList<Employee> getEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        if (!EMPLOYEE_FILE.exists() || EMPLOYEE_FILE.length() == 0) {
            return employees;
        }

        try (FileInputStream employeeFile = new FileInputStream(EMPLOYEE_FILE);
             ObjectInputStream input = new ObjectInputStream(employeeFile)) {
            Administrator admin = (Administrator) input.readObject(); // Ensure admin is handled appropriately.
            employees = (ArrayList<Employee>) input.readObject();

            for (Employee e : employees) {
                if (e instanceof Cashier)
                    System.out.println(((Cashier) e).getSector());
            }

        } catch (ClassNotFoundException | IOException e) {
            System.out.println(e.getMessage());
        }
        return employees;
    }

    public static void addEmployee(Employee e) {
        ArrayList<Employee> employees = getEmployees();
        employees.add(e);
        Administrator admin = getAdministrator(); // Retrieve the current admin to preserve it
        try (FileOutputStream employeeFile = new FileOutputStream(EMPLOYEE_FILE, false);
             ObjectOutputStream output = new ObjectOutputStream(employeeFile)) {
            output.writeObject(admin); // Write the admin first
            output.writeObject(employees); // Then write the employees list
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void deleteEmployee(Employee employee) {
        ArrayList<Employee> employees = getEmployees();
        employees.removeIf(e -> employee.getName().equalsIgnoreCase(e.getName())); // Remove the employee
        Administrator admin = getAdministrator(); // Retrieve the current admin to preserve it
        try (FileOutputStream employeeFile = new FileOutputStream(EMPLOYEE_FILE);
             ObjectOutputStream output = new ObjectOutputStream(employeeFile)) {
            System.out.println(employees.size());
            output.writeObject(admin); // Write the admin first
            output.writeObject(employees); // Then write the employees list
            for (Employee e : employees)
                System.out.println(e.getName());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Done");
    }

    public static Employee searchEmployee(String username, Role role) {
        ArrayList<Employee> employees = EmployeeDAO.getEmployees();
        for (Employee e : employees) {
            if (e.getUsername().equals(username) && e.getRole().equals(role)) {
                return e;
            }
        }
        return null;
    }

    public static Administrator getAdministrator() {
        Administrator admin = new Administrator();
        if (!EMPLOYEE_FILE.exists() || EMPLOYEE_FILE.length() == 0) {
            admin = new Administrator("Stela", "Kollaku", "stelaKollaku", "stelaKollaku", "admin@gmial.com", "3556856987", LocalDate.of(1998, 2, 12), 4000);
            addAdministrator(admin);
        }

        try (FileInputStream employeeFile = new FileInputStream(EMPLOYEE_FILE);
             ObjectInputStream input = new ObjectInputStream(employeeFile)) {
            admin = (Administrator) input.readObject();
        } catch (EOFException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return admin;
    }

    public static void addAdministrator(Administrator e) {
        ArrayList<Employee> employees = getEmployees(); // Retrieve current employees
        try (FileOutputStream employeeFile = new FileOutputStream(EMPLOYEE_FILE, false);
             ObjectOutputStream output = new ObjectOutputStream(employeeFile)) {
            output.writeObject(e); // Write the admin first
            output.writeObject(employees); // Then write the employees list
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static ArrayList<Employee> getEmployeesOfSectors(ArrayList<Sector> sectors) {
        ArrayList<Employee> employees = getEmployees();
        ArrayList<Employee> temp = new ArrayList<>();

        for (Employee e : employees) {
            if (e instanceof Cashier && sectors.contains(((Cashier) e).getSector()))
                temp.add(e);
        }
        return temp;
    }
}
