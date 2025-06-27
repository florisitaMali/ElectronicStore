package Views;

import DAO.EmployeeDAO;
import Models.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeesDialogBox {
    private final ButtonType saveButton = new ButtonType("Save");
    private final RadioButton cashierRadioButton = new RadioButton("Cashier");
    private final RadioButton managerRadioButton = new RadioButton("Manager");
    private final TextField nameField = new TextField();
    private final TextField surnameField = new TextField();
    private final TextField usernameField = new TextField();
    private final TextField passwordField = new TextField();
    private final DatePicker birthdayField = new DatePicker(LocalDate.of(1998, 10, 8));
    private final TextField phoneField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField salaryField = new TextField();
    private final ArrayList<CheckBox> permissionCheckBox = new ArrayList<>();
    private final CashierSectorSelection cashierSectorSelection = new CashierSectorSelection();
    private final ManagerSectorSelection managerSectorSelection = new ManagerSectorSelection();

    public ButtonType getSaveButton() {
        return saveButton;
    }
    public RadioButton getCashierRadioButton() {
        return cashierRadioButton;
    }
    public RadioButton getManagerRadioButton() {
        return managerRadioButton;
    }
    public TextField getNameField() {
        return nameField;
    }
    public TextField getSurnameField() {
        return surnameField;
    }
    public TextField getUsernameField() {
        return usernameField;
    }
    public TextField getPasswordField() {
        return passwordField;
    }
    public DatePicker getBirthdayField() {
        return birthdayField;
    }
    public TextField getPhoneField() {
        return phoneField;
    }
    public TextField getEmailField() {
        return emailField;
    }
    public TextField getSalaryField() {
        return salaryField;
    }
    public ArrayList<CheckBox> getPermissionCheckBox() {
        return permissionCheckBox;
    }
    public CashierSectorSelection getCashierSectorSelection() { return cashierSectorSelection; }
    public ManagerSectorSelection getManagerSectorSelection() { return managerSectorSelection; }

    public Dialog<Employee> createEmployee() {
        Dialog<Employee> employeeDialog = new Dialog<>();

        employeeDialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        nameField.setPromptText("First Name");
        surnameField.setPromptText("Last Name");
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        birthdayField.setPromptText("Date of Birth (YYYY-MM-DD)");
        phoneField.setPromptText("Phone number");
        emailField.setPromptText("Email");
        salaryField.setPromptText("Salary");

        ToggleGroup toggleGroup = new ToggleGroup();
        cashierRadioButton.setToggleGroup(toggleGroup);
        managerRadioButton.setToggleGroup(toggleGroup);

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(cashierRadioButton, managerRadioButton);

        VBox vbox = new VBox(5);
        for (Permission p : Permission.values()) {
            permissionCheckBox.add(new CheckBox(p.toString()));
        }
        vbox.getChildren().addAll(permissionCheckBox);

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("UserName:"), 0, 2);
        grid.add(usernameField, 1, 2);
        grid.add(new Label("PassWord:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Date of Birth:"), 0, 4);
        grid.add(birthdayField, 1, 4);
        grid.add(new Label("Phone number:"), 0, 5);
        grid.add(phoneField, 1, 5);
        grid.add(new Label("Email:"), 0, 6);
        grid.add(emailField, 1, 6);
        grid.add(new Label("Salary:"), 0, 7);
        grid.add(salaryField, 1, 7);
        grid.add(new Label("Role: "), 0, 8);
        grid.add(hbox, 1, 8);
        grid.add(new Label("Permission:"), 0, 9);
        grid.add(vbox, 1, 9);

        employeeDialog.getDialogPane().setContent(grid);

        employeeDialog.setResultConverter(dialogButton ->
        {
            if(dialogButton == saveButton) {
                try {
                    String name = nameField.getText();
                    String surname = surnameField.getText();
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    String email = emailField.getText();
                    String phoneNr = phoneField.getText();
                    LocalDate dateOfBirth = birthdayField.getValue();
                    double salary = Double.parseDouble(salaryField.getText());

                    if(name == null || surname == null || username == null || password == null || email == null || phoneNr == null || dateOfBirth == null || salary == 0)
                    {
                        ShowAlert.showAlert("Invalid data", "Please enter correct data");
                        return null;
                    }
                    if(!email.contains("@"))
                    {
                        ShowAlert.showAlert("Invalid data", "Please check again email.");
                        return null;
                    }
                    if(salary < 0)
                    {
                        ShowAlert.showAlert("Invalid data", "You cannot put negative salary.");
                        return null;
                    }

                    if (cashierRadioButton.isSelected()) {
                        Dialog<Sector> sectorDialog = cashierSectorSelection.createSectorDialog("Cashier working Sector");

                        if (sectorDialog.showAndWait() != null) {
                            System.out.println(sectorDialog.getResult());
                            if(sectorDialog.getResult() == null)
                            {
                                ShowAlert.showAlert("Invalid Data", "You haven't select the sectors");
                                return null;
                            }
                            Cashier cashier = new Cashier(name, surname, username, password, email, phoneNr, dateOfBirth, salary, sectorDialog.getResult());
                            for (int i = 0; i < permissionCheckBox.size(); i++) {
                                if (permissionCheckBox.get(i).isSelected()) {
                                    cashier.addPermission(Permission.values()[i]);
                                }
                            }
                            EmployeeDAO.addEmployee(cashier);
                            return cashier;
                        } else {
                            ShowAlert.showAlert("Not valid data", "The is not selected any sector.");
                        }
                    } else if (managerRadioButton.isSelected()) {
                        Dialog<ArrayList<Sector>> sectorDialog = managerSectorSelection.createSectorDialog("Manager working Sector");
                        if (sectorDialog.showAndWait() != null) {
                            ArrayList<Sector> sectors = sectorDialog.getResult();
                            if(sectors == null)
                            {
                                ShowAlert.showAlert("Invalid Data", "You haven't select the sectors");
                                return null;
                            }
                            Manager manager = new Manager(name, surname, username, password, email, phoneNr, dateOfBirth, salary);

                            for (Sector s : sectors) {
                                manager.addSector(s);
                            }
                            for (int i = 0; i < permissionCheckBox.size(); i++) {
                                if (permissionCheckBox.get(i).isSelected()) {
                                    manager.addPermission(Permission.values()[i]);
                                }
                            }
                            EmployeeDAO.addEmployee(manager);

                            return manager;
                        } else {
                            ShowAlert.showAlert("Not valid data", "The is not selected any employee.");
                        }
                    } else {
                        ShowAlert.showAlert("Invalid Selection", "Please select either Cashier or Manager.");
                    }
                    // Close the dialog after saving the employee
                } catch (NumberFormatException ex) {
                    ShowAlert.showAlert("Invalid Input", "Please enter valid numbers in Salary Field.");
                } catch (NotValidUsername ex) {
                    ShowAlert.showAlert("Invalid username", ex.getMessage());
                }
            }
            return null;
        });

        return employeeDialog;
    }
}
