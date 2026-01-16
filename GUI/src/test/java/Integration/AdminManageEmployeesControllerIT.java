package Integration;

import Controller.AdminManageEmployeesController;
import DAO.EmployeeDAO;
import Models.Employee;
import Views.AdminManageEmployeesView;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class AdminManageEmployeesControllerIT {

    private AdminManageEmployeesView view;
    private Employee adminUser;

    @Start
    void start(Stage stage) {
        // Use a real administrator from DB
        adminUser = EmployeeDAO.getAdministrator();
        assertNotNull(adminUser, "Admin user must exist in database");

        view = new AdminManageEmployeesView(adminUser);

        stage.setScene(new Scene(view.getView(), 1400, 800));
        stage.show();
    }

    @BeforeEach
    void refreshTable(FxRobot robot) {
        robot.interact(() -> view.loadData());
    }

    /* =========================
       SEARCH INTEGRATION TEST
       ========================= */
    @Test
    void searchFiltersEmployeesCorrectly(FxRobot robot) {
        TableView<Employee> table = view.getEmployeeTable();
        int originalSize = table.getItems().size();
        assertTrue(originalSize > 0);

        robot.clickOn(view.getSearchField()).write("a");

        assertTrue(
                table.getItems().size() <= originalSize,
                "Search must filter employees"
        );
    }

    /* =========================
       DELETE INTEGRATION TEST
       ========================= */
    @Test
    void deleteRemovesEmployeeFromTableAndDatabase(FxRobot robot) {
        TableView<Employee> table = view.getEmployeeTable();
        assertFalse(table.getItems().isEmpty());

        Employee toDelete = table.getItems().get(0);

        robot.clickOn(table);
        robot.interact(() -> table.getSelectionModel().select(toDelete));
        robot.clickOn(view.getDeleteButton());

        // UI assertion
        assertFalse(
                table.getItems().contains(toDelete),
                "Deleted employee must disappear from table"
        );

        // DB assertion
        assertFalse(
                EmployeeDAO.usernameExists(toDelete.getUsername()),
                "Deleted employee must be soft-deleted in DB"
        );
    }

    /* =========================
       ADD EMPLOYEE INTEGRATION
       ========================= */
    @Test
    void addEmployeeIncreasesTableSize(FxRobot robot) {
        int before = view.getEmployeeTable().getItems().size();

        robot.clickOn(view.getAddButton());

        // Dialog interaction
        writeEmployeeData(robot,
                "Test", "Employee", "test_user_123",
                "pass", "test@mail.com", "123456",
                "3000"
        );

        robot.clickOn("Save");

        int after = view.getEmployeeTable().getItems().size();

        assertEquals(before + 1, after);
        assertTrue(EmployeeDAO.usernameExists("test_user_123"));
    }

    /* =========================
       EDIT EMPLOYEE INTEGRATION
       ========================= */
    @Test
    void editEmployeeUpdatesTableData(FxRobot robot) {
        TableView<Employee> table = view.getEmployeeTable();
        Employee emp = table.getItems().get(0);

        robot.interact(() -> table.getSelectionModel().select(emp));
        robot.clickOn(view.getEditButton());

        double newSalary = emp.getSalary() + 500;
        robot.clickOn("#salaryField").eraseText(10).write(String.valueOf(newSalary));
        robot.clickOn("Save");

        Employee updated =
                table.getItems()
                        .stream()
                        .filter(e -> e.getId() == emp.getId())
                        .findFirst()
                        .orElseThrow();

        assertEquals(newSalary, updated.getSalary());
    }

    /* =========================
       HELPER METHOD
       ========================= */
    private void writeEmployeeData(FxRobot robot,
                                   String name, String surname, String username,
                                   String password, String email, String phone,
                                   String salary) {
        robot.clickOn("#nameField").write(name);
        robot.clickOn("#surnameField").write(surname);
        robot.clickOn("#usernameField").write(username);
        robot.clickOn("#passwordField").write(password);
        robot.clickOn("#emailField").write(email);
        robot.clickOn("#phoneField").write(phone);
        robot.clickOn("#salaryField").write(salary);
        robot.clickOn("Cashier");
    }
}
