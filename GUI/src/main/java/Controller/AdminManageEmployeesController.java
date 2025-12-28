package Controller;

import DAO.EmployeeDAO;
import Models.Employee;
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
        ArrayList<Employee> employees = EmployeeDAO.getEmployees();
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
        view.getAddButton().setOnAction(e->
        {
            new EmployeesDialogBox().createEmployee().showAndWait();
            view.getEmployeeTable().setItems(FXCollections.observableArrayList(EmployeeDAO.getEmployees()));
            view.loadData();
            view.getEmployeeTable().refresh();
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
            EmployeeDAO.deleteEmployee(selectedEmployee);
            view.getEmployeeTable().setItems(FXCollections.observableArrayList(EmployeeDAO.getEmployees()));
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
                EmployeesDialogBox editDialog = new EmployeesDialogBox();
                Dialog<Employee> dialog = editDialog.createEmployee();

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
                    editDialog.getPermissionCheckBox().get(i).setSelected(selectedEmployee.getAccessLevel().contains(Permission.values()[i]));
                }
                EmployeeDAO.deleteEmployee(selectedEmployee);

                //Show the dialog and wait for the result
                dialog.showAndWait().ifPresent(updatedEmployee -> {
                    if(updatedEmployee != null) {
                        EmployeeDAO.updateEmployee(updatedEmployee);
                        view.getEmployeeTable().setItems(FXCollections.observableArrayList(EmployeeDAO.getEmployees()));
                        view.getEmployeeTable().refresh();
                    }
                });
            }
        });
    }
}