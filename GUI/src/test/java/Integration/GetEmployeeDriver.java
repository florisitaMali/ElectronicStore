package Integration;

import DAO.EmployeeDAO;
import Models.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;

import static Models.Role.*;
import static org.junit.jupiter.api.Assertions.*;

public class GetEmployeeDriver {

    @Test
    void getEmployee_integrationWithPermissionsAndSectors() {
        Administrator admin = new Administrator(
                "Admin", "One", "admin", "pass", "admin@test.com", "111",
                LocalDate.of(1990, 1, 1), 5000
        );
        EmployeeDAO.addAdministrator(admin);

        Cashier cashierNoPerm = new Cashier(
                "Cash", "NoPerm", "cashnoperm", "pass", "cashnoperm@test.com", "222",
                LocalDate.of(1995, 5, 5), 3000, Sector.ELECTRONICS
        );
        EmployeeDAO.addEmployee(cashierNoPerm);

        Cashier cashierWithPerm = new Cashier(
                "Cash", "WithPerm", "cashperm", "pass", "cashperm@test.com", "333",
                LocalDate.of(1995, 6, 6), 3000, Sector.CAMERA
        );
        cashierWithPerm.addPermission(Permission.GENERATE_PRINTABLE_BILL);
        cashierWithPerm.addPermission(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY);
        EmployeeDAO.addEmployee(cashierWithPerm);

        Manager manager = new Manager(
                "Manager", "One", "manager", "pass", "manager@test.com", "444",
                LocalDate.of(1992, 3, 3), 4000
        );
        manager.addSector(Sector.CAMERA);
        manager.addSector(Sector.ELECTRONICS);
        EmployeeDAO.addEmployee(manager);

        List<Employee> employees = EmployeeDAO.getEmployees(admin);

        assertNotNull(employees);
        assertFalse(employees.isEmpty());

        for (Employee e : employees) {
            if (e instanceof Cashier cashier) {
                if (cashier.getUsername().equals("cashnoperm")) {
                    assertTrue(cashier.getAccessLevel().isEmpty());
                } else if (cashier.getUsername().equals("cashperm")) {
                    List<Permission> perms = cashier.getAccessLevel();
                    assertEquals(2, perms.size());
                    assertTrue(perms.contains(Permission.GENERATE_PRINTABLE_BILL));
                    assertTrue(perms.contains(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY));
                } else {
                    fail("Unexpected Cashier username: " + cashier.getUsername());
                }
            } else if (e instanceof Manager mgr) {
                assertEquals("manager", mgr.getUsername());
                List<Sector> sectors = mgr.getSectors();
                assertEquals(2, sectors.size());
                assertTrue(sectors.contains(Sector.CAMERA));
                assertTrue(sectors.contains(Sector.ELECTRONICS));
            } else if (e instanceof Administrator adm) {
                assertEquals("admin", adm.getUsername());
            } else {
                fail("Unknown employee type: " + e.getClass().getSimpleName());
            }
        }
    }
}
