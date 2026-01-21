package Integration;

import Controller.LoginController;
import DAO.DBConnection;
import DAO.EmployeeDAO;
import Models.Administrator;
import Models.Cashier;
import Models.Manager;
import Models.Role;
import Views.LoginView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.api.FxRobot;

import static org.junit.jupiter.api.Assertions.*;
import javafx.application.Platform;
import org.testfx.util.WaitForAsyncUtils;
import org.testfx.framework.junit5.Start;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;


@ExtendWith(ApplicationExtension.class)
class LoginControllerIT {

    private LoginView loginView;
    private LoginController controller;
    private Stage stage;

    @BeforeAll
    static void insertTestEmployees() {
        String sql = """
                INSERT INTO employees
                (first_name, last_name, username, password, email, phone, birth_date, salary, role_id, sector_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            //Admin
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "Admin");
                ps.setString(2, "One");
                ps.setString(3, "admin1");
                ps.setString(4, "admin1");
                ps.setString(5, "admin1@example.com");
                ps.setString(6, "1234567890");
                ps.setDate(7, Date.valueOf(LocalDate.of(1980, 1, 1)));
                ps.setDouble(8, 5000.0);
                ps.setInt(9, Role.ADMINISTRATOR.ordinal() + 1);
                ps.setInt(10, 1);
                ps.executeUpdate();
            }

            //Cashier
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "Cashier");
                ps.setString(2, "One");
                ps.setString(3, "cashier1");
                ps.setString(4, "cash123");
                ps.setString(5, "cashier1@example.com");
                ps.setString(6, "0987654321");
                ps.setDate(7, Date.valueOf(LocalDate.of(1990, 2, 2)));
                ps.setDouble(8, 3000.0);
                ps.setInt(9, 2);
                ps.setInt(10, 1);
                ps.executeUpdate();
            }

            //manager
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "Manager");
                ps.setString(2, "One");
                ps.setString(3, "mana1");
                ps.setString(4, "man1");
                ps.setString(5, "manager1@example.com");
                ps.setString(6, "1122334455");
                ps.setDate(7, Date.valueOf(LocalDate.of(1985, 3, 3)));
                ps.setDouble(8, 6000.0);
                ps.setInt(9, 3);
                ps.setInt(10, 1);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Inserted test employees for LoginControllerIT");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert test employees", e);
        }
    }

    @AfterAll
    static void cleanupTestEmployees() {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM employees WHERE username IN ('admin1', 'cashier1', 'mana1')"
            )) {
                int deleted = ps.executeUpdate();
                System.out.println("Deleted " + deleted + " test employees after LoginControllerIT");
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to clean up test employees", e);
        }
    }

    @Start
    void start(Stage stage) {
        loginView = new LoginView(Role.CASHIER);
        controller = new LoginController(loginView);
        this.stage = stage;

        stage.setScene(new Scene(loginView.getView(), 400, 300));
        stage.show();
    }

    @Test
    void administrator_emptyUsernameAndPassword(FxRobot robot) {
        loginView.setRole(Role.ADMINISTRATOR);

        robot.clickOn(loginView.getLoginBtn());

        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void administrator_wrongUsernameOrPassword(FxRobot robot) {
        loginView.setRole(Role.ADMINISTRATOR);

        robot.interact(() -> {
            loginView.getUserNameTextField().setText("wrongAdmin");
            loginView.getPasswordField().setText("wrongPass");
        });

        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void administrator_correctUsernameAndPassword(FxRobot robot) {
        loginView.setRole(Role.ADMINISTRATOR);

        robot.interact(() -> {
            loginView.getUserNameTextField().setText("admin1");
            loginView.getPasswordField().setText("admin1");
        });

        robot.clickOn(loginView.getLoginBtn());

        assertNotEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void cashier_emptyUsernameOrPassword(FxRobot robot) {
        loginView.setRole(Role.CASHIER);

        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void cashier_wrongUsernameOrPassword(FxRobot robot) {
        loginView.setRole(Role.CASHIER);

        robot.interact(() -> {
            loginView.getUserNameTextField().setText("wrongCash");
            loginView.getPasswordField().setText("wrong123");
        });

        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void cashier_correctUsernameAndPassword(FxRobot robot) {
        loginView.setRole(Role.CASHIER);

        robot.interact(() -> {
            loginView.getUserNameTextField().setText("cashier1");
            loginView.getPasswordField().setText("cash123");
        });

        robot.clickOn(loginView.getLoginBtn());
        assertNotEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void manager_emptyUsernameOrPassword(FxRobot robot) {
        loginView.setRole(Role.MANAGER);

        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void manager_wrongUsernameOrPassword(FxRobot robot) {
        loginView.setRole(Role.MANAGER);

        robot.interact(() -> {
            loginView.getUserNameTextField().setText("wrongMana");
            loginView.getPasswordField().setText("wrong123");
        });

        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void manager_correctUsernameAndPassword(FxRobot robot) {
        loginView.setRole(Role.MANAGER);

        robot.interact(() -> {
            loginView.getUserNameTextField().setText("mana1");
            loginView.getPasswordField().setText("man1");
        });

        robot.clickOn(loginView.getLoginBtn());
        assertNotEquals(stage.getScene().getRoot(), loginView.getView());
    }


    @Test
    void administratorRoleButCashierOrManagerCredentials(FxRobot robot) {
        loginView.setRole(Role.ADMINISTRATOR);

        //cashier credentials
        robot.interact(() -> {
            loginView.getUserNameTextField().setText("cashier1");
            loginView.getPasswordField().setText("cash123");
        });
        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());

        // manager credentials
        robot.interact(() -> {
            loginView.getUserNameTextField().setText("mana1");
            loginView.getPasswordField().setText("man1");
        });
        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void managerRoleButCashierOrAdministratorCredentials(FxRobot robot) {
        loginView.setRole(Role.MANAGER);

        //cashier credentials
        robot.interact(() -> {
            loginView.getUserNameTextField().setText("cashier1");
            loginView.getPasswordField().setText("cash123");
        });
        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());

        //admin credentials
        robot.interact(() -> {
            loginView.getUserNameTextField().setText("admin1");
            loginView.getPasswordField().setText("admin1");
        });
        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }

    @Test
    void cashierRoleButManagerOrAdministratorCredentials(FxRobot robot) {
        loginView.setRole(Role.CASHIER);

        //manager credentials
        robot.interact(() -> {
            loginView.getUserNameTextField().setText("mana1");
            loginView.getPasswordField().setText("man1");
        });
        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());

        //admin credentials
        robot.interact(() -> {
            loginView.getUserNameTextField().setText("admin1");
            loginView.getPasswordField().setText("admin1");
        });
        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }
}
