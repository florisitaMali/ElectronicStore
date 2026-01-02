package Models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdministratorTest {

    private Administrator admin;

    @BeforeEach
    void setUp() {
        //The default constructor
        admin = new Administrator();
    }

    @Test
    void defaultConstructor_shouldSetRoleAdministrator() {
        //Check if the role Administrator is assigned by the default constructor
        assertEquals(Role.ADMINISTRATOR, admin.getRole());
    }

    @Test
    void defaultConstructor_shouldAssignAllPermissions() {
        //We get the permissions assigned to the administrator
        ArrayList<Permission> permissions = admin.getAccessLevel();

        assertEquals(6, permissions.size());
        assertTrue(permissions.contains(Permission.GENERATE_PRINTABLE_BILL));
        assertTrue(permissions.contains(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY));
        assertTrue(permissions.contains(Permission.ADD_ITEMS_TO_STOCK));
        assertTrue(permissions.contains(Permission.GENERATE_TOTAL_COST_INCOME));
        assertTrue(permissions.contains(Permission.MANAGE_EMPLOYEES));
        assertTrue(permissions.contains(Permission.ACCESS_STATISTICS_ABOUT_SOLD_AND_PURCHASED_ITEMS));
    }

    @Test
    void parameterizedConstructor_shouldCreateAdministratorSuccessfully(){
        //Creating an administrator with the full constructor
        Administrator admin2 = new Administrator(
                "John",
                "Doe",
                "admin01",
                "password",
                "john@company.com",
                "123456789",
                LocalDate.of(1990, 1, 1),
                3000.0
        );

        //Check if the role Administrator is assigned
        assertEquals(Role.ADMINISTRATOR, admin2.getRole());
        //Check if all permissions are assigned
        assertEquals(6, admin2.getAccessLevel().size());
    }

    @Test
    void toString_shouldContainTotalCost() {
        String result = admin.toString();
        assertTrue(result.contains("Total Cost"));
    }
}
