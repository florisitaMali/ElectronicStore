package Integration;

import Controller.ProfileController;
import DAO.EmployeeDAO;
import Models.*;
import Views.Profile;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class ProfileControllerIT {

    private Profile view;
    private ProfileController controller;
    private Stage stage;

    private static final String BASE_USERNAME = "test_user";
    private static final String UPDATED_USERNAME = "updated_user";


    @Start
    void start(Stage stage) {
        this.stage = stage;

        Employee admin = createAdministrator();
        view = new Profile(admin);
        controller = new ProfileController(view);

        stage.setScene(new Scene(view.getView()));
        stage.show();
    }

    @AfterEach
    void cleanup() {
        executeDelete(
                "DELETE FROM Employees WHERE username = '" +
                        BASE_USERNAME + "' OR username = '" + UPDATED_USERNAME + "'"
        );
    }

    // -------------------- UI RELOAD --------------------

    private void loadProfile(Employee emp, FxRobot robot) {
        robot.interact(() -> {
            view = new Profile(emp);
            controller = new ProfileController(view);
            stage.setScene(new Scene(view.getView()));
            stage.show();
        });
    }

    // -------------------- TEST DATA --------------------

    private Administrator createAdministrator() {
        return new Administrator(
                "John", "Doe", BASE_USERNAME, "password123",
                "email@test.com", "1234567890",
                LocalDate.of(1990, 1, 1), 50000
        );
    }

    private Employee createEmployeeForRole(Role role) {
        Employee emp = switch (role) {
            case ADMINISTRATOR -> createAdministrator();
            case MANAGER -> new Manager(
                    "Jane", "Smith", BASE_USERNAME, "password123",
                    "manager@test.com", "0987654321",
                    LocalDate.of(1985, 5, 5), 60000
            );
            case CASHIER -> new Cashier(
                    "Bob", "Brown", BASE_USERNAME, "password123",
                    "cashier@test.com", "1122334455",
                    LocalDate.of(1995, 3, 3), 30000, Sector.COMPUTERS
            );
        };

        try {
            Employee existing = EmployeeDAO.searchEmployee(BASE_USERNAME, role);
            if (existing != null) {
                EmployeeDAO.softDeleteEmployee(existing);
            }
        } catch (Exception ignored) {}

        if (role == Role.ADMINISTRATOR) {
            EmployeeDAO.addAdministrator((Administrator) emp);
        } else {
            EmployeeDAO.addEmployee(emp);
        }

        return emp;
    }

    private void executeDelete(String sql) {
        try (var con = DAO.DBConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    // -------------------- TESTS --------------------

    @Test
    void changeUsername_Admin(FxRobot robot) {
        loadProfile(createEmployeeForRole(Role.ADMINISTRATOR), robot);// 🔑 NAVIGATE FIRST
        robot.clickOn("Username & Password");

        robot.clickOn("#changeUsernameBtn");
        robot.clickOn("#usernameField");
        robot.eraseText(50);
        robot.write(UPDATED_USERNAME);
        robot.clickOn("OK");

        assertEquals(UPDATED_USERNAME, view.getUsername().getText());

        Employee refreshed =
                EmployeeDAO.searchEmployee(UPDATED_USERNAME, Role.ADMINISTRATOR);
        assertNotNull(refreshed);
    }

    @Test
    void changeUsername_Cashier(FxRobot robot) {
        loadProfile(createEmployeeForRole(Role.CASHIER), robot);

        robot.clickOn("Username & Password");

        robot.clickOn("#changeUsernameBtn");
        robot.clickOn("#usernameField");
        robot.eraseText(50);
        robot.write(UPDATED_USERNAME);
        robot.clickOn("OK");

        Employee refreshed =
                EmployeeDAO.searchEmployee(UPDATED_USERNAME, Role.CASHIER);
        assertNotNull(refreshed);
    }

    @Test
    void changePassword_Manager(FxRobot robot) {
        loadProfile(createEmployeeForRole(Role.MANAGER), robot);

        robot.clickOn("Username & Password");

        robot.clickOn("#changePasswordBtn");
        robot.clickOn("#oldPasswordField");
        robot.write("password123");
        robot.clickOn("#passwordField");
        robot.write("newPass123");
        robot.clickOn("#confirmPasswordField");
        robot.write("newPass123");
        robot.clickOn("OK");

        Employee refreshed = EmployeeDAO.searchEmployee(BASE_USERNAME, Role.MANAGER);
        assertEquals("newPass123", refreshed.getPassword());
    }

    @Test
    void emptyUsername_doesNotUpdate(FxRobot robot) {
        loadProfile(createEmployeeForRole(Role.ADMINISTRATOR), robot);

        robot.clickOn("Username & Password");

        robot.clickOn("#changeUsernameBtn");
        robot.clickOn("#usernameField");
        robot.eraseText(50);
        robot.clickOn("OK");

        assertEquals(BASE_USERNAME, view.getUsername().getText());
    }

    @Test
    void emptyPassword_doesNotUpdate(FxRobot robot) {
        loadProfile(createEmployeeForRole(Role.MANAGER), robot);

        robot.clickOn("Username & Password");

        robot.clickOn("#changePasswordBtn");
        robot.clickOn("#passwordField");
        robot.eraseText(50);
        robot.clickOn("OK");

        Employee refreshed =
                EmployeeDAO.searchEmployee(BASE_USERNAME, Role.MANAGER);
        assertEquals("password123", refreshed.getPassword());
    }
}
