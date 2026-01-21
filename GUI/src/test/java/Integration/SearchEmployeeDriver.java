package Integration;

import DAO.EmployeeDAO;
import Models.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static Models.Role.*;
import static org.junit.jupiter.api.Assertions.*;

public class SearchEmployeeDriver {

    @Test
    void searchEmployee_integrationWithPermissionsAndSectors() {
        List<Employee> employees = new ArrayList<>();

        Administrator admin = new Administrator(
                "Admin", "One", "admin"+LocalDate.now().getDayOfYear(), "pass", "admin@test.com", "111",
                LocalDate.of(1990, 1, 1), 5000
        );
        EmployeeDAO.addAdministrator(admin);
        employees.add(admin);

        Cashier cashierNoPerm = new Cashier(
                "Cash", "NoPerm", "cashnoperm" + LocalDate.now().getDayOfYear(), "pass", "cashnoperm@test.com", "222",
                LocalDate.of(1995, 5, 5), 3000, Sector.ELECTRONICS
        );
        EmployeeDAO.addEmployee(cashierNoPerm);
        employees.add(cashierNoPerm);

        Cashier cashierWithPerm = new Cashier(
                "Cash", "WithPerm", "cashperm" + LocalDate.now().getDayOfYear(), "pass", "cashperm@test.com", "333",
                LocalDate.of(1995, 6, 6), 3000, Sector.CAMERA
        );
        cashierWithPerm.addPermission(Permission.GENERATE_PRINTABLE_BILL);
        cashierWithPerm.addPermission(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY);
        EmployeeDAO.addEmployee(cashierWithPerm);
        employees.add(cashierWithPerm);

        Manager manager = new Manager(
                "Manager", "One", "manager" + LocalDate.now().getDayOfYear(), "pass", "manager@test.com", "444",
                LocalDate.of(1992, 3, 3), 4000
        );
        manager.addSector(Sector.CAMERA);
        manager.addSector(Sector.ELECTRONICS);
        EmployeeDAO.addEmployee(manager);
        employees.add(manager);


        for (Employee em : employees) {
            Employee e = EmployeeDAO.searchEmployee(em.getUsername(), em.getRole());
            if (e instanceof Cashier cashier) {
                if (cashier.getUsername().equals("cashnoperm" + LocalDate.now().getDayOfYear())) {
                    assertTrue(cashier.getAccessLevel().isEmpty());
                } else if (cashier.getUsername().equals("cashperm"+LocalDate.now().getDayOfYear())) {
                    List<Permission> perms = cashier.getAccessLevel();
                    assertEquals(2, perms.size());
                    assertTrue(perms.contains(Permission.GENERATE_PRINTABLE_BILL));
                    assertTrue(perms.contains(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY));
                } else {
                    fail("Unexpected Cashier username: " + cashier.getUsername());
                }
            } else if (e instanceof Manager mgr) {
                assertEquals("manager"+LocalDate.now().getDayOfYear(), mgr.getUsername());
                List<Sector> sectors = mgr.getSectors();
                assertEquals(2, sectors.size());
                assertTrue(sectors.contains(Sector.CAMERA));
                assertTrue(sectors.contains(Sector.ELECTRONICS));
            } else if (e instanceof Administrator adm) {
                assertEquals("admin"+LocalDate.now().getDayOfYear(), adm.getUsername());
            } else {
                fail("Unknown employee type: " + e.getClass().getSimpleName());
            }
        }
    }
}
