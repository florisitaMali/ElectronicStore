package DAO;

import Models.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static Models.Role.*;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeDAOTest {

    @BeforeEach
    void setup() {
        deleteEmployees();
    }

    @Test
    void searchEmployee_returnsEmployeeForEachRole() {
        Administrator admin = new Administrator("Admin", "One", "admin", "pass", "a@a.com", "111",
                LocalDate.of(1990, 1, 1), 5000);
        Cashier cashier = new Cashier("Cash", "One", "cash", "pass", "c@c.com", "222",
                LocalDate.of(1995, 5, 5), 3000, Sector.ELECTRONICS);
        Manager managert = new Manager("Manager", "One", "man", "man", "m@m.com", "333",
                LocalDate.of(1998, 5, 5), 4000);
        EmployeeDAO.addAdministrator(admin);
        EmployeeDAO.addEmployee(cashier);
        EmployeeDAO.addEmployee(managert);

        assertNotNull(EmployeeDAO.searchEmployee("admin", ADMINISTRATOR));
        assertNotNull(EmployeeDAO.searchEmployee("cash", CASHIER));
        assertNotNull(EmployeeDAO.searchEmployee("man", MANAGER));
    }

    @Test
    void searchEmployee_nonExisting_returnsNull() {
        assertNull(EmployeeDAO.searchEmployee("missing", CASHIER));
    }

    @Test
    void addAdministrator_uniqueUsername_successfullyAdded() {
        Administrator admin = new Administrator(
                "Admin", "One", "admin1", "pass",
                "admin1@test.com", "111",
                LocalDate.of(1990, 1, 1), 5000
        );

        assertDoesNotThrow(() -> EmployeeDAO.addAdministrator(admin));

        Employee retrieved = EmployeeDAO.searchEmployee("admin1", ADMINISTRATOR);
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof Administrator);
        assertEquals("admin1", retrieved.getUsername());
    }

    @Test
    void addAdministrator_duplicateUsername_throwsException() {
        Administrator admin1 = new Administrator(
                "Admin", "One", "admin1", "pass",
                "admin1@test.com", "111",
                LocalDate.of(1990, 1, 1), 5000
        );

        Administrator admin2 = new Administrator(
                "Admin", "Two", "admin1", "pass2",
                "admin2@test.com", "222",
                LocalDate.of(1991, 2, 2), 6000
        );

        EmployeeDAO.addAdministrator(admin1);

        assertThrows(RuntimeException.class,
                () -> EmployeeDAO.addAdministrator(admin2));
    }


    @Test
    void getAdministrator_whenExists_returnsAdmin() {
        Administrator admin = new Administrator("Admin", "One", "admin", "pass", "a@a.com", "111",
                LocalDate.of(1990, 1, 1), 5000);
        EmployeeDAO.addAdministrator(admin);

        Administrator retrieved = EmployeeDAO.getAdministrator();
        assertNotNull(retrieved);
        assertEquals("admin", retrieved.getUsername());
    }

    @Test
    void getAdministrator_whenNotExists_returnsNull() {
        assertNull(EmployeeDAO.getAdministrator());
    }

    @Test
    void getEmployees_emptyDatabase_returnsEmptyList() {
        Administrator admin = new Administrator(
                "Admin", "One", "admin1", "pass",
                "admin@test.com", "111",
                LocalDate.of(1990, 1, 1), 5000
        );
        EmployeeDAO.addAdministrator(admin);

        List<Employee> employees = EmployeeDAO.getEmployees(admin);

        assertNotNull(employees);
        assertTrue(employees.isEmpty());
    }

    @Test
    void getEmployees_existEmployee_returnsEmployees() {
        Administrator admin = new Administrator(
                "Admin", "One", "admin1", "pass",
                "admin@test.com", "111",
                LocalDate.of(1990, 1, 1), 5000
        );
        EmployeeDAO.addAdministrator(admin);

        Cashier cashier = new Cashier(
                "Cash", "One", "cash1", "pass",
                "cash@test.com", "222",
                LocalDate.of(1995, 5, 5), 3000,
                Sector.ELECTRONICS
        );
        EmployeeDAO.addEmployee(cashier);

        List<Employee> employees = EmployeeDAO.getEmployees(admin);

        assertEquals(1, employees.size());
        assertTrue(employees.get(0) instanceof Cashier);
        assertEquals("cash1", employees.get(0).getUsername());
    }

    @Test
    void addEmployee_uniqueUsername_succeeds() {
        Cashier cashier = new Cashier("Cash", "One", "cash1", "pass", "c@c.com", "222",
                LocalDate.of(1995, 5, 5), 3000, Sector.CAMERA);

        assertDoesNotThrow(() -> EmployeeDAO.addEmployee(cashier));
    }

    @Test
    void addEmployee_duplicateUsername_throwsException() {
        Cashier c1 = new Cashier("Cash", "One", "cash", "pass", "c1@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.CAMERA);
        Cashier c2 = new Cashier("Cash", "Two", "cash", "pass", "c2@c.com", "222",
                LocalDate.of(1996, 6, 6), 3000, Sector.ELECTRONICS);

        EmployeeDAO.addEmployee(c1);
        assertThrows(RuntimeException.class, () -> EmployeeDAO.addEmployee(c2));
    }

    @Test
    void updateEmployee_whenEmployeeDoesNotExist_DoesNothing() {
        Cashier cashier = new Cashier("Ghost", "User", "ghost", "pass", "g@g.com", "999",
                LocalDate.of(1990, 1, 1), 2000, Sector.CAMERA);

        assertDoesNotThrow(() -> EmployeeDAO.updateEmployee(cashier));
    }

    @Test
    void updateEmployee_whenEmployeeExistsWithUniqueUsername_updatesSuccessfully() {
        Cashier cashier = new Cashier(
                "Cash", "One", "cash1", "pass", "c1@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.ELECTRONICS
        );
        EmployeeDAO.addEmployee(cashier);

        cashier.setEmail("updated@c.com");
        cashier.setSalary(4200);

        assertDoesNotThrow(() -> EmployeeDAO.updateEmployee(cashier));

        Employee updated = EmployeeDAO.searchEmployee("cash1", Role.CASHIER);
        assertNotNull(updated);
        assertEquals("updated@c.com", updated.getEmail());
        assertEquals(4200, updated.getSalary());
    }

    @Test
    void updateEmployee_whenDuplicateUsername_throwsException() {
        Cashier cashier1 = new Cashier(
                "Cash", "One", "cash1", "pass", "c1@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.CAMERA
        );
        Cashier cashier2 = new Cashier(
                "Cash", "Two", "cash2", "pass", "c2@c.com", "222",
                LocalDate.of(1996, 6, 6), 3000, Sector.ELECTRONICS
        );

        EmployeeDAO.addEmployee(cashier1);
        EmployeeDAO.addEmployee(cashier2);

        cashier2.setUsername("cash1");

        assertThrows(RuntimeException.class,
                () -> EmployeeDAO.updateEmployee(cashier2));
    }

    @Test
    void softDeleteEmployee_whenEmployeeDoesNotExist_doesNothing() {
        Cashier cashier = new Cashier("Ghost", "User", "ghost", "pass", "g@g.com", "999",
                LocalDate.of(1990, 1, 1), 2000, Sector.CAMERA);

        assertDoesNotThrow(() -> EmployeeDAO.softDeleteEmployee(cashier));
    }

    @Test
    void softDeleteEmployee_whenEmployeeExists_employeeIsHidden() {
        Cashier cashier = new Cashier(
                "Cash", "One", "cash1", "pass", "c1@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.ELECTRONICS
        );

        EmployeeDAO.addEmployee(cashier);

        assertNotNull(EmployeeDAO.searchEmployee("cash1", Role.CASHIER));

        assertDoesNotThrow(() -> EmployeeDAO.softDeleteEmployee(cashier));

        Employee deleted = EmployeeDAO.searchEmployee("cash1", Role.CASHIER);
        assertNull(deleted);
    }



    @Test
    void usernameExists_emptyDatabase_returnsFalse() {
        assertFalse(EmployeeDAO.usernameExists("any"));
    }

    @Test
    void usernameExists_existing_returnsTrue() {
        Cashier cashier = new Cashier("Cash", "One", "cash", "pass", "c@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.CAMERA);
        EmployeeDAO.addEmployee(cashier);

        assertTrue(EmployeeDAO.usernameExists("cash"));
    }

    @Test
    void usernameExists_nonExistingUsername_returnsFalse() {
        Cashier cashier = new Cashier(
                "Cash", "One", "cash", "pass", "c@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.CAMERA
        );
        EmployeeDAO.addEmployee(cashier);

        assertFalse(EmployeeDAO.usernameExists("unknown"));
    }

    @Test
    void usernameExistsExceptSelf_existingOtherUser_returnsTrue() {
        Cashier c1 = new Cashier("Cash", "One", "cash1", "pass", "c1@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.CAMERA);
        Cashier c2 = new Cashier("Cash", "Two", "cash2", "pass", "c2@c.com", "222",
                LocalDate.of(1996, 6, 6), 3000, Sector.ELECTRONICS);

        EmployeeDAO.addEmployee(c1);
        EmployeeDAO.addEmployee(c2);

        Employee e1 = EmployeeDAO.searchEmployee("cash1", CASHIER);
        Employee e2 = EmployeeDAO.searchEmployee("cash2", CASHIER);

        assertNotNull(e1);
        assertNotNull(e2);

        assertTrue(EmployeeDAO.usernameExistsExceptSelf("cash1", e2.getId()));
        assertFalse(EmployeeDAO.usernameExistsExceptSelf("cash2", e2.getId()));
    }

    @Test
    void usernameExistsExceptSelf_nonExistingUsername_returnsFalse() {
        Cashier c1 = new Cashier(
                "Cash", "One", "cash1", "pass", "c1@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.CAMERA
        );
        EmployeeDAO.addEmployee(c1);

        Employee e1 = EmployeeDAO.searchEmployee("cash1", CASHIER);
        assertNotNull(e1);

        assertFalse(EmployeeDAO.usernameExistsExceptSelf("ghost", e1.getId()));
    }

    @Test
    void getEmployeesOfSectors_emptyDatabase_returnsEmptyList() {
        List<Employee> employees =
                EmployeeDAO.getEmployeesOfSectors(List.of(Sector.ELECTRONICS));

        assertNotNull(employees);
        assertTrue(employees.isEmpty());
    }

    @Test
    void getEmployeesOfSectors_noEmployeeInSector_returnsEmptyList() {
        Cashier cashier1 = new Cashier(
                "Cash", "One", "cash1", "pass", "c1@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.CAMERA
        );
        Cashier cashier2 = new Cashier(
                "Cash", "Two", "cash2", "pass", "c2@c.com", "222",
                LocalDate.of(1996, 6, 6), 3000, Sector.CAMERA
        );

        EmployeeDAO.addEmployee(cashier1);
        EmployeeDAO.addEmployee(cashier2);

        List<Employee> employees =
                EmployeeDAO.getEmployeesOfSectors(List.of(Sector.ELECTRONICS));

        assertNotNull(employees);
        assertTrue(employees.isEmpty());
    }

    @Test
    void getEmployeesOfSectors_withEmployeesInSector_returnsOnlyMatchingEmployees() {
        Cashier cashier1 = new Cashier(
                "Cash", "One", "cash1", "pass", "c1@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.ELECTRONICS
        );
        Cashier cashier2 = new Cashier(
                "Cash", "Two", "cash2", "pass", "c2@c.com", "222",
                LocalDate.of(1996, 6, 6), 3000, Sector.CAMERA
        );
        Cashier cashier3 = new Cashier(
                "Cash", "Three", "cash3", "pass", "c3@c.com", "333",
                LocalDate.of(1997, 7, 7), 3000, Sector.ELECTRONICS
        );

        EmployeeDAO.addEmployee(cashier1);
        EmployeeDAO.addEmployee(cashier2);
        EmployeeDAO.addEmployee(cashier3);

        List<Employee> employees =
                EmployeeDAO.getEmployeesOfSectors(List.of(Sector.ELECTRONICS));

        assertEquals(2, employees.size());
        assertTrue(employees.stream().allMatch(e ->
                ((Cashier) e).getSector() == Sector.ELECTRONICS
        ));
    }

    private void deleteEmployees() {
        String sql1 = "DELETE FROM employee_permissions";
        String sql2 = "DELETE FROM employee_sectors";
        String sql3 = "DELETE FROM employees";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps1 = con.prepareStatement(sql1);
             PreparedStatement ps2 = con.prepareStatement(sql2);
             PreparedStatement ps3 = con.prepareStatement(sql3)) {

            ps1.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
        } catch (Exception ignored) {}
    }

    @Test
    void updateEmployee_updatesPermissionsCorrectly() {
        Cashier cashier = new Cashier(
                "Cash", "One", "cash1", "pass", "c@c.com", "111",
                LocalDate.of(1995, 5, 5), 3000, Sector.CAMERA
        );

        cashier.addPermission(Permission.GENERATE_PRINTABLE_BILL);
        EmployeeDAO.addEmployee(cashier);

        Employee saved = EmployeeDAO.searchEmployee("cash1", CASHIER);
        assertNotNull(saved);

        saved.getAccessLevel().clear();
        saved.addPermission(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY);
        saved.addPermission(Permission.ENTER_NEW_ITEM_CATEGORIES);

        EmployeeDAO.updateEmployee(saved);

        Employee updated = EmployeeDAO.searchEmployee("cash1", CASHIER);
        assertNotNull(updated);

        List<Permission> permissions = updated.getAccessLevel();

        assertEquals(2, permissions.size());
        assertTrue(permissions.contains(Permission.ENTER_NEW_ITEM_CATEGORIES));
        assertTrue(permissions.contains(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY));
        assertFalse(permissions.contains(Permission.GENERATE_PRINTABLE_BILL));
    }

}
