package Controller;

import DAO.EmployeeDAO;
import Models.Employee;
import Models.NotValidUsername;
import Models.Permission;
import Models.Role;
import Views.AdminManageEmployeesView;
import Views.EmployeesDialogBox;
import Views.ShowAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Dialog;

import java.util.ArrayList;

public class AdminManageEmployeesController {
    AdminManageEmployeesView view;
    private EmployeesDialogBox dialogBox = new EmployeesDialogBox();
    private Dialog<Employee> employeeDialog = dialogBox.createEmployee();

    public Dialog<Employee> getEmployeeDialog() {
        return employeeDialog;
    }

    public EmployeesDialogBox getDialogBox() {
        return dialogBox;
    }
    
    public Dialog<Employee> createAddEmployeeDialogForTest() {
        employeeDialog = dialogBox.createEmployee();
        return employeeDialog;
    }

    public AdminManageEmployeesController(AdminManageEmployeesView view)
    {
        this.view = view;
        enableButtons();
    }

    private void enableButtons()
    {
        addSearchButtonAction();
        addAddButtonAction();
        addDeleteButtonAction();
        addEditButtonAction();
    }

    private void addSearchButtonAction() {
        view.getSearchField().textProperty().addListener((observable, oldValue, newValue) -> {
            searchEmployee(newValue);
        });
    }

    private void searchEmployee(String searchText) {
        ArrayList<Employee> employees = EmployeeDAO.getEmployees(view.getCurrentUser());
        if (searchText.isEmpty()) {
            view.getEmployeeTable().setItems(FXCollections.observableArrayList(employees));
        } else {
            ObservableList<Employee> filteredList = FXCollections.observableArrayList(employees)
                    .filtered(employee -> employee.getName().toLowerCase().contains(searchText.toLowerCase()));
            view.getEmployeeTable().setItems(filteredList);
        }
    }

    private void addAddButtonAction()
    {
        view.getAddButton().setOnAction(e -> {
            employeeDialog = dialogBox.createEmployee();

            employeeDialog.showAndWait().ifPresent(employee -> {
                try {
                    EmployeeDAO.addEmployee(employee);
                } catch (NotValidUsername ex) {
                    ShowAlert.showAlert("Error", ex.getMessage());
                    return;
                }

                view.getEmployeeTable().setItems(
                        FXCollections.observableArrayList(
                                EmployeeDAO.getEmployees(view.getCurrentUser())
                        )
                );
                view.getEmployeeTable().refresh();

                dialogBox.clearFields();
            });
        });

    }

    private void addDeleteButtonAction()
    {
        view.getDeleteButton().setOnAction(e->
        {
            Employee selectedEmployee = view.getEmployeeTable().getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                ShowAlert.showAlert("No Selection", "Please select an employee to delete.");
                return;
            }
            EmployeeDAO.softDeleteEmployee(selectedEmployee);
            view.getEmployeeTable().setItems(FXCollections.observableArrayList(EmployeeDAO.getEmployees(view.getCurrentUser())));
            view.getEmployeeTable().refresh();
        });
    }

    private void addEditButtonAction()
    {
        view.getEditButton().setOnAction(e->
        {
            Employee selectedEmployee = view.getEmployeeTable().getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                ShowAlert.showAlert("No Selection", "Please select an employee to edit.");
            } else {
                //Proceed with editing the selected employee
                EmployeesDialogBox editDialog = dialogBox;
                editDialog.setEditingEmployee(selectedEmployee);

                employeeDialog = editDialog.createEmployee();

                //Pre-fill the dialog fields with the selected employees data
                editDialog.getNameField().setText(selectedEmployee.getName());
                editDialog.getSurnameField().setText(selectedEmployee.getSurname());
                editDialog.getUsernameField().setText(selectedEmployee.getUsername());
                editDialog.getPasswordField().setText(selectedEmployee.getPassword());
                editDialog.getBirthdayField().setValue(selectedEmployee.getDateOfBirth());
                editDialog.getPhoneField().setText(selectedEmployee.getPhoneNumber());
                editDialog.getEmailField().setText(selectedEmployee.getEmail());
                editDialog.getSalaryField().setText(String.valueOf(selectedEmployee.getSalary()));
                editDialog.getCashierRadioButton().setSelected(selectedEmployee.getRole().equals(Role.CASHIER));
                editDialog.getManagerRadioButton().setSelected(selectedEmployee.getRole().equals(Role.MANAGER));

                for(int i=0; i<Permission.values().length; i++)
                {
                    System.out.println("Setting permission " + Permission.values()[i] + " to " + selectedEmployee.getAccessLevel().contains(Permission.values()[i]));
                    editDialog.getPermissionCheckBox().get(i).setSelected(selectedEmployee.getAccessLevel().contains(Permission.values()[i]));
                }

                employeeDialog.showAndWait().ifPresent(updatedEmployee -> {
                    if (updatedEmployee != null) {

                        updatedEmployee.setId(selectedEmployee.getId());

                        EmployeeDAO.updateEmployee(updatedEmployee);

                        view.getEmployeeTable().setItems(
                                FXCollections.observableArrayList(
                                        EmployeeDAO.getEmployees(view.getCurrentUser())
                                )
                        );
                        view.getEmployeeTable().refresh();
                    }
                });
                editDialog.setEditingEmployee(null);
                dialogBox.clearFields();
            }
        });
    }

}