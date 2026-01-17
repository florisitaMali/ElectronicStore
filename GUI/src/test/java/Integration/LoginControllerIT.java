package Integration;

import Controller.LoginController;
import DAO.EmployeeDAO;
import Models.Administrator;
import Models.Cashier;
import Models.Manager;
import Models.Role;
import Views.LoginView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.api.FxRobot;

import static org.junit.jupiter.api.Assertions.*;
import javafx.application.Platform;
import org.testfx.util.WaitForAsyncUtils;
import org.testfx.framework.junit5.Start;


@ExtendWith(ApplicationExtension.class)
class LoginControllerIT {

    private LoginView loginView;
    private LoginController controller;
    private Stage stage;


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

        // Should move to EmployeesMainPage
        assertNotEquals(stage.getScene().getRoot(), loginView.getView());
    }

    // ================= CASHIER LOGIN TESTS =================

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

    // ================= MANAGER LOGIN TESTS =================

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

    // ================= CROSS-ROLE TESTS =================

    @Test
    void administratorRoleButCashierOrManagerCredentials(FxRobot robot) {
        loginView.setRole(Role.ADMINISTRATOR);

        // cashier credentials
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

        // cashier credentials
        robot.interact(() -> {
            loginView.getUserNameTextField().setText("cashier1");
            loginView.getPasswordField().setText("cash123");
        });
        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());

        // admin credentials
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

        // manager credentials
        robot.interact(() -> {
            loginView.getUserNameTextField().setText("mana1");
            loginView.getPasswordField().setText("man1");
        });
        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());

        // admin credentials
        robot.interact(() -> {
            loginView.getUserNameTextField().setText("admin1");
            loginView.getPasswordField().setText("admin1");
        });
        robot.clickOn(loginView.getLoginBtn());
        assertEquals(stage.getScene().getRoot(), loginView.getView());
    }
}
