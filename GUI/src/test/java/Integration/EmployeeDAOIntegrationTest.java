package Integration;

import DAO.DBConnection;
import DAO.EmployeeDAO;
import Models.*;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static Models.Role.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeDAOIntegrationTest {

    private static Administrator testAdmin;
    private static Cashier testCashier;
    private static Manager testManager;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Ensure roles exist
            stmt.execute("INSERT IGNORE INTO roles(id, name) VALUES (1, 'ADMINISTRATOR')");
            stmt.execute("INSERT IGNORE INTO roles(id, name) VALUES (2, 'CASHIER')");
            stmt.execute("INSERT IGNORE INTO roles(id, name) VALUES (3, 'MANAGER')");

            // Ensure sector exists for cashier/manager
            stmt.execute("INSERT IGNORE INTO sectors(name) VALUES ('ELECTRONICS')");
        }

        // Create test employees
        testAdmin = new Administrator("Admin", "Test", "admin_test", "pass123", "admin@test.com", "1234567890",
                LocalDate.of(1990, 1, 1), 5000);

        testCashier = new Cashier("Cashier", "Test", "cashier_test", "pass123", "cashier@test.com", "1234567890",
                LocalDate.of(1995, 2, 2), 2000, Sector.COMPUTERS);

        testManager = new Manager("Manager", "Test", "manager_test", "pass123", "manager@test.com", "1234567890",
                LocalDate.of(1985, 3, 3), 4000);
    }

    @Test
    @Order(1)
    public void testAddAdministrator() {
        EmployeeDAO.addAdministrator(testAdmin);

        Administrator fetched = (Administrator) EmployeeDAO.searchEmployee("admin_test", ADMINISTRATOR);
        assertNotNull(fetched);
        assertEquals(testAdmin.getUsername(), fetched.getUsername());
    }

    @Test
    @Order(2)
    public void testAddCashierAndManager() throws Exception {
        EmployeeDAO.addEmployee(testCashier);
        EmployeeDAO.addEmployee(testManager);

        Cashier fetchedCashier = (Cashier) EmployeeDAO.searchEmployee("cashier_test", CASHIER);
        Manager fetchedManager = (Manager) EmployeeDAO.searchEmployee("manager_test", Role.MANAGER);

        assertNotNull(fetchedCashier);
        assertEquals(Sector.COMPUTERS, fetchedCashier.getSector());

        assertNotNull(fetchedManager);
    }

    @Test
    @Order(3)
    public void testGetEmployeesExcludesSelf() {
        List<Employee> employees = EmployeeDAO.getEmployees(testAdmin);

        // Should not include testAdmin
        assertTrue(employees.stream().noneMatch(e -> e.getUsername().equals("admin_test")));

        // Should include other employees
        assertTrue(employees.stream().anyMatch(e -> e.getUsername().equals("cashier_test")));
        assertTrue(employees.stream().anyMatch(e -> e.getUsername().equals("manager_test")));
    }

    @Test
    @Order(4)
    public void testUpdateEmployee() {
        testCashier.setSalary(2500);
        testCashier.getAccessLevel().add(Permission.GENERATE_PRINTABLE_BILL); // example permission
        EmployeeDAO.updateEmployee(testCashier);

        Cashier updated = (Cashier) EmployeeDAO.searchEmployee("cashier_test", CASHIER);
        assertEquals(2500, updated.getSalary());
        assertTrue(updated.getAccessLevel().contains(Permission.GENERATE_PRINTABLE_BILL));
    }

    @Test
    @Order(5)
    public void testSoftDeleteEmployee() {
        EmployeeDAO.softDeleteEmployee(testManager);

        Manager deleted = (Manager) EmployeeDAO.searchEmployee("manager_test", Role.MANAGER);
        assertNull(deleted); // should be null after soft delete
    }

    @AfterAll
    public static void cleanupDatabase() throws Exception {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Delete test employees
            stmt.execute("DELETE FROM employees WHERE username LIKE '%_test'");

            // Optionally, remove test sectors (only if safe)
            stmt.execute("DELETE FROM sectors WHERE name='ELECTRONICS' AND id > 0"); // avoid real sectors
        }
    }
}

