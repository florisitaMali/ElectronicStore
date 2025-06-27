package Controller;

import DAO.EmployeeDAO;
import Models.Administrator;
import Models.Employee;
import Models.NotValidUsername;
import Views.ChangePassword;
import Views.Profile;
import Views.ShowAlert;
import Views.UsenameChange;
import javafx.scene.control.Dialog;

public class ProfileController {
    private final Profile profile;

    public ProfileController(Profile profile){
        this.profile = profile;
        enableButtons();
    }

    private void enableButtons()
    {
        setPersonalInfoBtnAction();
        setWorkRelatedInfoBtnAction();
        setSecurityInfoBtnAction();
        setChangeUsernameAction();
        setChangePassWordAction();
    }
    private void setPersonalInfoBtnAction()
    {
        profile.getPersonalDetails().setOnAction(e->profile.getMainPane().setCenter(profile.getPersonalInfo()));
    }

    private void setWorkRelatedInfoBtnAction()
    {
        profile.getWorkRelatedDetails().setOnAction(e->profile.getMainPane().setCenter(profile.getOtherInfo()));
    }

    private void setSecurityInfoBtnAction()
    {
        profile.getUsernamePassWord().setOnAction(e->profile.getMainPane().setCenter(profile.getSecurityInfo()));
    }
    private void setChangeUsernameAction() {
        profile.getChangeUsername().setOnAction(e -> {
            Dialog<String> dialog = new UsenameChange().changeUsername(profile.getCurrentUser().getUsername());
            dialog.showAndWait().ifPresent(newUsername -> {
                if (newUsername != null && !newUsername.trim().isEmpty()) {
                    try {
                        if (profile.getCurrentUser() instanceof Administrator) {
                            Administrator admin = EmployeeDAO.getAdministrator();
                            admin.setUsername(newUsername);
                            profile.getUsername().setText(admin.getUsername());
                            EmployeeDAO.addAdministrator(admin);
                            Administrator newO = EmployeeDAO.getAdministrator();
                            profile.setCurrentUser(newO);
                            System.out.println("US: " + newO.getUsername() + " Pass: " + newO.getPassword());
                            ShowAlert.showAlert("Username Changed", "Username has been changed successfully to " + newUsername);
                        } else {
                            Employee emp = EmployeeDAO.searchEmployee(profile.getCurrentUser().getUsername(), profile.getCurrentUser().getRole());
                            EmployeeDAO.deleteEmployee(emp);
                            emp.setUsername(newUsername);
                            EmployeeDAO.addEmployee(emp);
                            profile.setCurrentUser(emp);
                            profile.getUsername().setText(emp.getUsername());
                            ShowAlert.showAlert("Username Changed", "Username has been changed successfully to " + newUsername);
                        }
                    } catch (NotValidUsername ex) {
                        ShowAlert.showAlert("Invalid Username", ex.getMessage());
                    }
                } else {
                    ShowAlert.showAlert("Invalid Input", "Username cannot be empty.");
                }
            });
        });
    }

    private void setChangePassWordAction() {
        profile.getChangePassword().setOnAction(e -> {
            Dialog<String> dialog = new ChangePassword().changePassword(profile.getCurrentUser());
            dialog.showAndWait().ifPresent(newPassword -> {
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    try {
                        if (profile.getCurrentUser() instanceof Administrator) {
                            Administrator admin = EmployeeDAO.getAdministrator();
                            admin.setPassword(newPassword);
                            EmployeeDAO.addAdministrator(admin);
                            Administrator newO = EmployeeDAO.getAdministrator();
                            profile.setCurrentUser(newO);
                            System.out.println("US: " + newO.getUsername() + " Pass: " + newO.getPassword());
                            ShowAlert.showAlert("Password Changed", "Password has been changed successfully to " + newPassword);
                        } else {
                            Employee emp = profile.getCurrentUser();
                            EmployeeDAO.deleteEmployee(emp);
                            emp.setPassword(newPassword);
                            EmployeeDAO.addEmployee(emp);
                            profile.setCurrentUser(emp);
                            ShowAlert.showAlert("Password Changed", "Password has been changed successfully to " + newPassword);
                        }
                    } catch (Exception ex) {
                        ShowAlert.showAlert("Error", ex.getMessage());
                    }
                } else {
                    ShowAlert.showAlert("Invalid Input", "Password cannot be empty.");
                }
            });
        });
    }

}
