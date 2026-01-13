package Integration;

import FakeClasses.FakeEmployeeDAO;
import Models.Employee;
import Models.Role;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeRepositoryIntegrationTest {

    @Test
    void getEmployees_ReturnsCorrectEmployeesWithRolesAndSalaries() {
        // Bottom-Up Integration: Fake DAO as lower-level component
        FakeEmployeeDAO fakeEmployeeDAO = new FakeEmployeeDAO();

        // Act
        ArrayList<Employee> employees = fakeEmployeeDAO.getEmployees();
        Employee administrator = fakeEmployeeDAO.getAdministrator();

        // Assert: repository returns data
        assertNotNull(employees);
        assertEquals(4, employees.size());

        // Assert: roles are correctly integrated
        assertEquals(Role.CASHIER, employees.get(0).getRole());
        assertEquals(Role.CASHIER, employees.get(1).getRole());
        assertEquals(Role.MANAGER, employees.get(2).getRole());
        assertEquals(Role.ADMINISTRATOR, employees.get(3).getRole());

        // Assert: salary behavior based on real integration
        for (Employee e : employees) {
            if (e.getRole() != Role.ADMINISTRATOR) {
                assertTrue(e.getSalary() > 0);
            }
        }

        // Assert: administrator exists and role is correct
        assertNotNull(administrator);
        assertEquals(Role.ADMINISTRATOR, administrator.getRole());
    }
}
