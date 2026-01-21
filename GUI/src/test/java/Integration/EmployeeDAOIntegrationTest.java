package Integration;

import DAO.DBConnection;
import DAO.EmployeeDAO;
import Models.*;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
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
        try (Connection con = DBConnection.getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO roles(id, name) VALUES (1, 'ADMINISTRATOR') ON DUPLICATE KEY UPDATE name=name");
            stmt.execute("INSERT INTO roles(id, name) VALUES (2, 'CASHIER') ON DUPLICATE KEY UPDATE name=name");
            stmt.execute("INSERT INTO roles(id, name) VALUES (3, 'MANAGER') ON DUPLICATE KEY UPDATE name=name");

            stmt.execute("INSERT INTO sectors(name) VALUES ('ELECTRONICS') ON DUPLICATE KEY UPDATE name=name");
        }

        testAdmin = new Administrator(
                "Admin", "Test", "admin_test", "pass123", "admin@test.com", "1234567890",
                LocalDate.of(1990, 1, 1), 5000
        );

        testCashier = new Cashier(
                "Cashier", "Test", "cashier_test", "pass123", "cashier@test.com", "1234567890",
                LocalDate.of(1995, 2, 2), 2000, Sector.ELECTRONICS
        );

        testManager = new Manager(
                "Manager", "Test", "manager_test", "pass123", "manager@test.com", "1234567890",
                LocalDate.of(1985, 3, 3), 4000
        );
    }

    @Test
    @Order(1)
    public void testAddAdministrator() {
        EmployeeDAO.addAdministrator(testAdmin);

        Administrator admin = (Administrator) EmployeeDAO.searchEmployee("admin_test", ADMINISTRATOR);
        assertNotNull(admin);
        assertEquals(testAdmin.getUsername(), admin.getUsername());
    }

    @Test
    @Order(2)
    public void testAddCashierAndManager() throws Exception {
        EmployeeDAO.addEmployee(testCashier);
        EmployeeDAO.addEmployee(testManager);

        Cashier cashier = (Cashier) EmployeeDAO.searchEmployee("cashier_test", CASHIER);
        Manager mana = (Manager) EmployeeDAO.searchEmployee("manager_test", MANAGER);

        assertNotNull(cashier);
        assertEquals(Sector.ELECTRONICS, cashier.getSector());

        assertNotNull(mana);
    }

    @Test
    @Order(3)
    public void testGetEmployeesExcludesSelf() {
        List<Employee> employees = EmployeeDAO.getEmployees(testAdmin);

        assertTrue(employees.stream().noneMatch(e -> e.getUsername().equals("admin_test")));
        assertTrue(employees.stream().anyMatch(e -> e.getUsername().equals("cashier_test")));
        assertTrue(employees.stream().anyMatch(e -> e.getUsername().equals("manager_test")));
    }

    @Test
    @Order(4)
    public void testUpdateEmployee() {
        testCashier.setSalary(2500);
        testCashier.getAccessLevel().add(Permission.GENERATE_PRINTABLE_BILL);
        EmployeeDAO.updateEmployee(testCashier);

        Cashier updated = (Cashier) EmployeeDAO.searchEmployee("cashier_test", CASHIER);
        assertEquals(2500, updated.getSalary());
        assertTrue(updated.getAccessLevel().contains(Permission.GENERATE_PRINTABLE_BILL));
    }

    @Test
    @Order(5)
    public void testSoftDeleteEmployee() {
        EmployeeDAO.softDeleteEmployee(testManager);

        Manager deleted = (Manager) EmployeeDAO.searchEmployee("manager_test", MANAGER);
        assertNull(deleted);
    }

    @AfterAll
    public static void cleanupDatabase() throws Exception {
        try (Connection con = DBConnection.getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate("DELETE es FROM employee_sectors es  JOIN sectors s ON s.id = es.sector_id WHERE s.name='ELECTRONICS' ");
            stmt.executeUpdate("DELETE FROM employees WHERE username LIKE '%_test'");
            System.out.println("EmployeeDAOIntegrationTest: Cleanup completed successfully");
        }
    }
}
