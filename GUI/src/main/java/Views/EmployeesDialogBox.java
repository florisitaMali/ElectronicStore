package Views;

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

    private Sector testSelectedSector; // For tests
    private ArrayList<Sector> testSelectedSectors; // For manager

    public void setTestSelectedSector(Sector sector) {
        this.testSelectedSector = sector;
    }

    public void setTestSelectedSectors(ArrayList<Sector> sectors) {
        this.testSelectedSectors = sectors;
    }

    private Employee editingEmployee = null;

    public void setEditingEmployee(Employee employee) {
        this.editingEmployee = employee;
    }

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

        nameField.setId("nameField");
        surnameField.setId("surnameField");
        usernameField.setId("usernameField");
        passwordField.setId("passwordField");
        birthdayField.setId("birthdayField");
        phoneField.setId("phoneField");
        emailField.setId("emailField");
        salaryField.setId("salaryField");

        cashierRadioButton.setId("Cashier");
        managerRadioButton.setId("Manager");


        for (int i = 0; i < permissionCheckBox.size(); i++) {
            permissionCheckBox.get(i).setId(Permission.values()[i].name());
        }

        ToggleGroup toggleGroup = new ToggleGroup();
        cashierRadioButton.setToggleGroup(toggleGroup);
        managerRadioButton.setToggleGroup(toggleGroup);

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(cashierRadioButton, managerRadioButton);

        VBox vbox = new VBox(5);
        permissionCheckBox.clear();
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

        employeeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                try {
                    String name = nameField.getText().trim();
                    String surname = surnameField.getText().trim();
                    String username = usernameField.getText().trim();
                    String password = passwordField.getText().trim();
                    String email = emailField.getText().trim();
                    String phoneNr = phoneField.getText().trim();
                    String salaryText = salaryField.getText().trim();
                    LocalDate dateOfBirth = birthdayField.getValue();

                    // ======== VALIDATION ========
                    if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()
                            || email.isEmpty() || phoneNr.isEmpty() || salaryText.isEmpty() || dateOfBirth == null
                            || (!cashierRadioButton.isSelected() && !managerRadioButton.isSelected())) {
                        ShowAlert.showAlert("Invalid Data", "Please fill in all required fields and select a role.");
                        return null;
                    }

                    double salary;
                    try {
                        salary = Double.parseDouble(salaryText);
                        if (salary < 0) {
                            ShowAlert.showAlert("Invalid Data", "Salary must be a positive number.");
                            return null;
                        }
                    } catch (NumberFormatException ex) {
                        ShowAlert.showAlert("Invalid Data", "Salary must be a valid number.");
                        return null;
                    }

                    // ======== CREATE EMPLOYEE ========
                    if (cashierRadioButton.isSelected()) {
                        Sector sector = (testSelectedSector != null)
                                ? testSelectedSector
                                : cashierSectorSelection.createSectorDialog("Cashier Sector")
                                .showAndWait()
                                .orElse(null);

                        if (sector == null) {
                            ShowAlert.showAlert("Invalid Data", "Please select a sector for the cashier.");
                            return null;
                        }

                        Cashier cashier = new Cashier(name, surname, username, password,
                                email, phoneNr, dateOfBirth, salary, sector);
                        if (editingEmployee != null) cashier.setId(editingEmployee.getId());

                        for (int i = 0; i < permissionCheckBox.size(); i++) {
                            if (permissionCheckBox.get(i).isSelected()) cashier.addPermission(Permission.values()[i]);
                        }
                        return cashier;
                    }

                    if (managerRadioButton.isSelected()) {
                        ArrayList<Sector> sectors = (testSelectedSectors != null)
                                ? testSelectedSectors
                                : managerSectorSelection.createSectorDialog("Manager Sectors")
                                .showAndWait()
                                .orElse(null);

                        if (sectors == null || sectors.isEmpty()) {
                            ShowAlert.showAlert("Invalid Data", "Please select at least one sector for the manager.");
                            return null;
                        }

                        Manager manager = new Manager(name, surname, username, password,
                                email, phoneNr, dateOfBirth, salary);
                        if (editingEmployee != null) manager.setId(editingEmployee.getId());
                        sectors.forEach(manager::addSector);

                        for (int i = 0; i < permissionCheckBox.size(); i++) {
                            if (permissionCheckBox.get(i).isSelected()) manager.addPermission(Permission.values()[i]);
                        }
                        return manager;
                    }

                } catch (Exception ex) {
                    ShowAlert.showAlert("Invalid Data", ex.getMessage());
                    return null;
                }
            }
            return null;
        });

        return employeeDialog;
    }

    public void clearFields() {
        nameField.clear();
        surnameField.clear();
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        phoneField.clear();
        salaryField.clear();
        birthdayField.setValue(LocalDate.of(1998, 10, 8)); // default birthday
        cashierRadioButton.setSelected(false);
        managerRadioButton.setSelected(false);

        for (CheckBox cb : permissionCheckBox) {
            cb.setSelected(false);
        }

        // Clear test sectors for testing purposes
        testSelectedSector = null;
        if (testSelectedSectors != null) testSelectedSectors.clear();
    }

}
