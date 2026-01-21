package Controller;

import DAO.EmployeeDAO;
import Models.Administrator;
import Models.Employee;
import Models.Role;
import Models.SoldItem;
import Views.EmployeesMainPage;
import Views.LoginView;
import Views.MainPageView;
import Views.ShowAlert;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;

public class LoginController {
    private final LoginView loginView;

    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        enableLoginButton();
        enableBackBtn();
    }

    private void enableLoginButton() {
        loginView.getLoginBtn().setOnAction(e -> {
            String username = loginView.getUserNameTextField().getText();
            String password = loginView.getPasswordField().getText();

            System.out.println("Login button clicked");
            Administrator admin;

            if(loginView.getRole().equals(Role.ADMINISTRATOR)) {
                admin = EmployeeDAO.getAdministrator();
                //System.out.println(admin.getUsername() + "  " + admin.getPassword());
                if(username.equals(admin.getUsername()) && password.equals(admin.getPassword()))
                {
                    admin = EmployeeDAO.getAdministrator();
                    showEmployeesMainPage(admin);
                    return;
                }
                else
                {
                    loginView.getUserNameTextField().clear();
                    loginView.getPasswordField().clear();
                }
            }

            Employee emp = EmployeeDAO.searchEmployee(username, loginView.getRole());

            if (emp != null) {
                System.out.println("Employee found: " + emp.getUsername());

                if (emp.getPassword().equals(password)) {
                    System.out.println("Password matches");
                    showEmployeesMainPage(emp);
                } else {
                    ShowAlert.showAlert("Invalid password", "Please check again password.");
                    loginView.getUserNameTextField().clear();
                    loginView.getPasswordField().clear();
                }
            } else {
                ShowAlert.showAlert("Invalid data", "Please check again username and password.");
                System.out.println("Employee not found");
                loginView.getUserNameTextField().clear();
                loginView.getPasswordField().clear();
            }
        });
    }

    private void showEmployeesMainPage(Employee emp) {
        EmployeesMainPage mainPage = new EmployeesMainPage(emp);
        Stage stage = (Stage) loginView.getLoginBtn().getScene().getWindow();
        Scene scene = new Scene(mainPage.getView(), 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void enableBackBtn()
    {
        loginView.getBackBtn().setOnAction(e->
        {
            MainPageView mainPage = new MainPageView();
            Stage stage = (Stage) loginView.getBackBtn().getScene().getWindow();
            new MainPageController(mainPage, stage);
            Scene scene = new Scene(mainPage.getView(), 800, 500);
            stage.setScene(scene);
            stage.show();
        });
    }
}
