package Integration;

import Controller.ProfileController;
import DAO.EmployeeDAO;
import Models.Administrator;
import Models.Employee;
import Models.NotValidUsername;
import Models.Role;
import Views.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileControllerIT extends ApplicationTest {

    private Profile profile;
    private ProfileController controller;
    private Administrator admin;

    @BeforeEach
    public void setup() throws Exception {
        // Ensure admin username does not conflict
        String baseUsername = "admin_test";
        String uniqueUsername = baseUsername + "_" + System.currentTimeMillis();

        // Clean up if an old admin exists (soft delete)
        try {
            Employee existing = EmployeeDAO.searchEmployee(baseUsername, Role.ADMINISTRATOR);
            if (existing != null) {
                EmployeeDAO.softDeleteEmployee(existing);
            }
        } catch (NotValidUsername ignored) {
        }

        // Create a fresh administrator for testing
        // Option 2: full constructor
        Administrator admin = new Administrator(
                "John", "Doe", "admin_test", "password123", "admin@email.com",
                "1234567890", LocalDate.of(1990, 1, 1), 50000
        );

        EmployeeDAO.addAdministrator(admin);

        // Initialize Profile and Controller
        profile = new Profile(admin);
        controller = new ProfileController(profile);
    }

    @Test
    public void changeUsername_updatesUIAndDatabase() {
        // Simulate dialog input
        String newUsername = "updated_" + System.currentTimeMillis();

        // Create dialog for test
        controller.createUsernameDialogForTest(profile.getCurrentUser().getUsername())
                .setResult(newUsername);

        // Trigger username change action
        profile.getChangeUsername().getOnAction().handle(null);

        // Verify UI updated
        assertEquals(newUsername, profile.getUsername().getText());

        // Verify database updated
        Administrator refreshed = EmployeeDAO.getAdministrator();
        assertEquals(newUsername, refreshed.getUsername());
    }

    @Test
    public void changePassword_updatesPassword() {
        String newPassword = "newPass123";

        // Create dialog for test
        controller.createPasswordDialogForTest(profile.getCurrentUser())
                .setResult(newPassword);

        // Trigger password change
        profile.getChangePassword().getOnAction().handle(null);

        // Verify database updated
        Administrator refreshed = EmployeeDAO.getAdministrator();
        assertEquals(newPassword, refreshed.getPassword());
    }
}
