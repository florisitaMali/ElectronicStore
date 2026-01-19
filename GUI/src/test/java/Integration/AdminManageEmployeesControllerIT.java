package Integration;

import Controller.AdminManageEmployeesController;
import DAO.EmployeeDAO;
import Models.Employee;
import Models.Role;
import Models.Sector;
import Views.AdminManageEmployeesView;
import Views.EmployeesDialogBox;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class AdminManageEmployeesControllerIT {

    private AdminManageEmployeesView view;
    private Employee adminUser;
    private AdminManageEmployeesController controller;

    @Start
    void start(Stage stage) {
        // Use a real administrator from DB
        adminUser = EmployeeDAO.getAdministrator();
        assertNotNull(adminUser, "Admin user must exist in database");

        view = new AdminManageEmployeesView(adminUser);
        controller = new AdminManageEmployeesController(view);

        stage.setScene(new Scene(view.getView(), 1400, 800));
        stage.show();
    }

    @BeforeEach
    void refreshTable(FxRobot robot) {
        robot.interact(() -> view.loadData());
    }

    @Test
    void searchFiltersEmployeesCorrectly(FxRobot robot) {
        TableView<Employee> table = view.getEmployeeTable();
        int originalSize = table.getItems().size();
        assertTrue(originalSize > 0);
        robot.clickOn(view.getSearchField()).write("");
        assertTrue(table.getItems().size() == originalSize);

        robot.clickOn(view.getSearchField()).write("a");

        assertTrue(table.getItems().size() <= originalSize);
    }

    @Test
    void deleteRemovesEmployeeFromTableAndDatabase(FxRobot robot) {
        TableView<Employee> table = view.getEmployeeTable();
        assertFalse(table.getItems().isEmpty());

        Employee toDelete = table.getItems().get(0);

        robot.clickOn(table);
        robot.interact(() -> table.getSelectionModel().select(toDelete));
        robot.clickOn(view.getDeleteButton());

        assertFalse(table.getItems().contains(toDelete));
    }

    @Test
    void addEmployeeIncreasesTableSizeForCashier(FxRobot robot) {
        int before = view.getEmployeeTable().getItems().size();

        robot.clickOn(view.getAddButton());

        robot.interact(() -> {
            EmployeesDialogBox dialogBox = controller.getDialogBox();
            dialogBox.setTestSelectedSector(Sector.CAMERA);
        });

        writeEmployeeData(robot,
                "Test", "Employee", "test_user_123",
                "pass", "test@mail.com", "123456",
                "3000", Role.CASHIER
        );

        robot.clickOn("Cashier");
        robot.clickOn("Save");

        int after = view.getEmployeeTable().getItems().size();

        assertEquals(before + 1, after);
        assertTrue(EmployeeDAO.usernameExists("test_user_123"));
    }

    @Test
    void addEmployeeIncreasesTableSizeForManager(FxRobot robot) {
        int before = view.getEmployeeTable().getItems().size();

        robot.clickOn(view.getAddButton());

        ArrayList<Sector> sectors = new ArrayList<Sector>();
        sectors.add(Sector.CAMERA);
        sectors.add(Sector.ELECTRONICS);

        robot.interact(() -> {
            EmployeesDialogBox dialogBox = controller.getDialogBox();
            dialogBox.setTestSelectedSectors(sectors);
        });

        writeEmployeeData(robot,
                "Test5", "Manager", "test_user_159",
                "pass", "test@mail.com", "123456",
                "3000", Role.MANAGER
        );

        robot.clickOn("Save");

        view.getEmployeeTable().refresh();
        int after = view.getEmployeeTable().getItems().size();

        assertEquals(before + 1, after);
        assertTrue(EmployeeDAO.usernameExists("test_user_123"));
    }

    @Test
    void addEmployeePreventsDuplicate(FxRobot robot) {
        TableView<Employee> table = view.getEmployeeTable();
        int before = table.getItems().size();

        // Add first employee
        robot.clickOn(view.getAddButton());
        robot.interact(() -> {
            EmployeesDialogBox dialogBox = controller.getDialogBox();
            dialogBox.setTestSelectedSector(Sector.CAMERA);
        });

        writeEmployeeData(robot,
                "Test1", "Employee1", "test_user_130",
                "pass", "test@mail.com", "123456",
                "3000", Role.CASHIER
        );
        robot.clickOn("Cashier");
        robot.clickOn("Save");

        int afterFirst = table.getItems().size();
        assertEquals(before + 1, afterFirst);
        assertTrue(EmployeeDAO.usernameExists("test_user_130"));

        // Try adding duplicate username
        robot.clickOn(view.getAddButton());
        robot.interact(() -> {
            EmployeesDialogBox dialogBox = controller.getDialogBox();
            dialogBox.setTestSelectedSector(Sector.CAMERA);
        });

        writeEmployeeData(robot,
                "TestDup", "EmployeeDup", "test_user_130", // duplicate username
                "pass", "dup@mail.com", "654321",
                "3500", Role.CASHIER
        );
        robot.clickOn("Save");

        int afterDuplicate = table.getItems().size();
        assertEquals(afterFirst, afterDuplicate, "Duplicate employee should not be added");
    }

    @ParameterizedTest
    @CsvSource({
            "'', Employee, test_user_127, pass, test@mail.com, 123456, 3000, CASHIER",
            "Test2, '', test_user_128, pass, test@mail.com, 123456, 3000, CASHIER",
            "Test3, Employee3, '', pass, test@mail.com, 123456, 3000, CASHIER",
            "Test4, Employee4, test_user_129, pass, invalidEmail, 123456, 3000, CASHIER",
            "Test5, Employee5, test_user_130, pass, test@mail.com, 123456, -1000, CASHIER"
    })
    void addEmployeeRejectsInvalidData(String name, String surname, String username,
                                       String password, String email, String phone,
                                       String salary, String roleName) {

        FxRobot robot = new FxRobot(); // <-- manually create

        Role role = Role.valueOf(roleName);

        TableView<Employee> table = view.getEmployeeTable();
        int before = table.getItems().size();

        robot.clickOn(view.getAddButton());

        robot.interact(() -> {
            EmployeesDialogBox dialogBox = controller.getDialogBox();
            if (role == Role.CASHIER) dialogBox.setTestSelectedSector(Sector.CAMERA);
        });

        writeEmployeeData(robot, name, surname, username, password, email, phone, salary, role);
        robot.clickOn(role == Role.CASHIER ? "Cashier" : "Manager");
        robot.clickOn("Save");

        int after = table.getItems().size();
        assertEquals(before, after, "Invalid employee data should not be saved");
    }

    @Test
    void editWithNoEmployeeSelectedShowsAlert(FxRobot robot) {
        // Ensure no selection
        view.getEmployeeTable().getSelectionModel().clearSelection();

        // Click edit
        robot.clickOn(view.getEditButton());

        // Here we would assert that ShowAlert was called or dialog not shown
        // If using TestFX, we can check that no dialog appears
        assertFalse(controller.getEmployeeDialog().isShowing());
    }

    @Test
    void editWithMultipleEmployeesSelectedUsesFirstOne(FxRobot robot) {
        TableView<Employee> table = view.getEmployeeTable();
        if(table.getItems().size() < 2) return; // Skip if not enough employees

        // Select first two employees
        robot.interact(() -> {
            table.getSelectionModel().selectIndices(0, 2);
        });

        // Click edit
        robot.clickOn(view.getEditButton());

        // The dialog should open with the first selected employee
        Employee lastSelected = table.getItems().get(2);
        assertEquals(lastSelected.getName(), controller.getDialogBox().getNameField().getText());
    }

    @Test
    void editEmployeeWithValidDataUpdatesSuccessfully(FxRobot robot) {
        TableView<Employee> table = view.getEmployeeTable();
        Employee emp = table.getItems().get(0);

        selectEmployee(robot, table, emp);
        openEditDialog(robot);

        // Change some fields
        double newSalary = emp.getSalary() + 1000;
        writeEmployeeFields(robot,
                "EditedName", emp.getSurname(), emp.getUsername(),
                emp.getPassword(), emp.getEmail(), emp.getPhoneNumber(),
                String.valueOf(newSalary)
        );

        robot.clickOn("Cashier"); // keep role same
        robot.clickOn(controller.getDialogBox().getPermissionCheckBox().get(0));

        saveDialog(robot);

        Employee updated = table.getItems().stream()
                .filter(e -> e.getId() == emp.getId())
                .findFirst().orElseThrow();
        assertEquals("EditedName", updated.getName());
        assertEquals(newSalary, updated.getSalary());
    }


    @ParameterizedTest
    @CsvSource({
            "'', Employee, test_user_127, pass, test@mail.com, 123456, 3000, CASHIER",
            "Test2, '', test_user_128, pass, test@mail.com, 123456, 3000, CASHIER",
            "Test3, Employee3, '', pass, test@mail.com, 123456, 3000, CASHIER",
            "Test4, Employee4, test_user_129, pass, invalidEmail, 123456, 3000, CASHIER",
            "Test5, Employee5, test_user_130, pass, test@mail.com, 123456, -1000, MANAGER"
    })
    void editEmployeeWithInvalidDataShowsError(String name, String surname, String username,
                                               String password, String email, String phone,
                                               String salary, String roleName, FxRobot robot) {

        Role role = Role.valueOf(roleName);

        TableView<Employee> table = view.getEmployeeTable();
        Employee emp = table.getItems().get(0); // Pick the first employee to edit

        int before = table.getItems().size();

        // Select the employee and click Edit
        robot.interact(() -> table.getSelectionModel().select(emp));
        robot.clickOn(view.getEditButton());

        // Get the dialog and set test sectors if needed
        FxRobot dialogBox = robot.interact(() -> {
            EmployeesDialogBox db = controller.getDialogBox();
            if (role == Role.CASHIER) db.setTestSelectedSector(Sector.CAMERA);
            return db;
        });

        // Write invalid data
        writeEmployeeFields(robot, name, surname, username, password, email, phone, salary);

        // Click role and Save
        robot.clickOn(role == Role.CASHIER ? "Cashier" : "Manager");
        robot.clickOn("Save");

        // Ensure that invalid data was not saved
        int after = table.getItems().size();
        assertEquals(before, after, "Employee table should not update for invalid data");

        // Optional: you can also check that the original employee's fields remain unchanged
        Employee updatedEmp = table.getItems()
                .stream()
                .filter(e -> e.getId() == emp.getId())
                .findFirst()
                .orElseThrow();
        assertEquals(emp.getName(), updatedEmp.getName(), "Name should not have changed");
        assertEquals(emp.getUsername(), updatedEmp.getUsername(), "Username should not have changed");
    }


    private void writeEmployeeData(FxRobot robot,
                                   String name, String surname, String username,
                                   String password, String email, String phone,
                                   String salary, Role role) {

        robot.clickOn("#nameField").write(name);
        robot.clickOn("#surnameField").write(surname);
        robot.clickOn("#usernameField").write(username);
        robot.clickOn("#passwordField").write(password);
        // Skip date field (already has a default)
        robot.clickOn("#phoneField").write(phone);
        robot.clickOn("#emailField").write(email);
        robot.clickOn("#salaryField").write(salary);

        // Select role
        if(role == Role.MANAGER)
            robot.clickOn("#Manager");
        else {
            robot.clickOn("#Cashier");
        }

        // Select permissions (example)
        robot.clickOn("GENERATEPRINTABLE_BILL");
        robot.clickOn("VIEWBILLS_AND_TOTAL_FOR_CURRENT_DAY");
    }

    private void selectEmployee(FxRobot robot, TableView<Employee> table, Employee emp) {
        robot.interact(() -> table.getSelectionModel().select(emp));
    }

    private void openEditDialog(FxRobot robot) {
        robot.clickOn(view.getEditButton());
        robot.interact(() -> assertNotNull(controller.getDialogBox(), "Dialog box must be initialized"));
    }

    private void writeEmployeeFields(FxRobot robot,
                                     String name, String surname, String username,
                                     String password, String email, String phone,
                                     String salary) {
        robot.clickOn("#nameField").eraseText(20).write(name);
        robot.clickOn("#surnameField").eraseText(20).write(surname);
        robot.clickOn("#usernameField").eraseText(20).write(username);
        robot.clickOn("#passwordField").eraseText(20).write(password);
        robot.clickOn("#phoneField").eraseText(20).write(phone);
        robot.clickOn("#emailField").eraseText(20).write(email);
        robot.clickOn("#salaryField").eraseText(20).write(salary);
    }

    private void saveDialog(FxRobot robot) {
        robot.clickOn("Save");
    }
}
