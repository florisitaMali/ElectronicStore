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

    // ✅ Dialog references (testable)
    private Dialog<String> usernameDialog;
    private Dialog<String> passwordDialog;

    public ProfileController(Profile profile){
        this.profile = profile;
        enableButtons();
    }

    /* =======================
       TEST SUPPORT METHODS
       ======================= */

    public Dialog<String> getUsernameDialog() {
        return usernameDialog;
    }

    public Dialog<String> getPasswordDialog() {
        return passwordDialog;
    }

    public Dialog<String> createUsernameDialogForTest(String currentUsername) {
        usernameDialog = new UsenameChange().changeUsername(currentUsername);
        return usernameDialog;
    }

    public Dialog<String> createPasswordDialogForTest(Employee user) {
        passwordDialog = new ChangePassword().changePassword(user);
        return passwordDialog;
    }


    private void enableButtons() {
        setPersonalInfoBtnAction();
        setWorkRelatedInfoBtnAction();
        setSecurityInfoBtnAction();
        setChangeUsernameAction();
        setChangePassWordAction();
    }

    private void setPersonalInfoBtnAction() {
        profile.getPersonalDetails()
                .setOnAction(e -> profile.getMainPane().setCenter(profile.getPersonalInfo()));
    }

    private void setWorkRelatedInfoBtnAction() {
        profile.getWorkRelatedDetails()
                .setOnAction(e -> profile.getMainPane().setCenter(profile.getOtherInfo()));
    }

    private void setSecurityInfoBtnAction() {
        profile.getUsernamePassWord()
                .setOnAction(e -> profile.getMainPane().setCenter(profile.getSecurityInfo()));
    }

    private void setChangeUsernameAction() {
        profile.getChangeUsername().setOnAction(e -> {

            usernameDialog =
                    new UsenameChange().changeUsername(profile.getCurrentUser().getUsername());

            usernameDialog.showAndWait().ifPresent(newUsername -> {

                if (newUsername == null || newUsername.trim().isEmpty()) {
                    ShowAlert.showAlert("Invalid Input", "Username cannot be empty.");
                    return;
                }

                try {
                    if (profile.getCurrentUser() instanceof Administrator) {

                        Administrator admin = EmployeeDAO.getAdministrator();
                        admin.setUsername(newUsername);
                        EmployeeDAO.updateEmployee(admin);

                        Administrator refreshed = EmployeeDAO.getAdministrator();
                        profile.setCurrentUser(refreshed);
                        profile.getUsername().setText(refreshed.getUsername());

                    } else {

                        Employee emp = EmployeeDAO.searchEmployee(
                                profile.getCurrentUser().getUsername(),
                                profile.getCurrentUser().getRole());

                        emp.setUsername(newUsername);
                        EmployeeDAO.updateEmployee(emp);

                        profile.setCurrentUser(emp);
                        profile.getUsername().setText(emp.getUsername());
                    }

                    ShowAlert.showAlert(
                            "Username Changed",
                            "Username has been changed successfully to " + newUsername
                    );

                } catch (NotValidUsername ex) {
                    ShowAlert.showAlert("Invalid Username", ex.getMessage());
                }
            });
        });
    }

    private void setChangePassWordAction() {
        profile.getChangePassword().setOnAction(e -> {

            passwordDialog =
                    new ChangePassword().changePassword(profile.getCurrentUser());

            passwordDialog.showAndWait().ifPresent(newPassword -> {

                if (newPassword == null || newPassword.trim().isEmpty()) {
                    ShowAlert.showAlert("Invalid Input", "Password cannot be empty.");
                    return;
                }

                try {
                    if (profile.getCurrentUser() instanceof Administrator) {

                        Administrator admin = EmployeeDAO.getAdministrator();
                        admin.setPassword(newPassword);
                        EmployeeDAO.updateEmployee(admin);

                        Administrator refreshed = EmployeeDAO.getAdministrator();
                        profile.setCurrentUser(refreshed);

                    } else {
                        Employee emp = profile.getCurrentUser();
                        emp.setPassword(newPassword);
                        EmployeeDAO.updateEmployee(emp);

                        profile.setCurrentUser(emp);
                    }

                    ShowAlert.showAlert(
                            "Password Changed",
                            "Password has been changed successfully."
                    );

                } catch (Exception ex) {
                    ShowAlert.showAlert("Error", ex.getMessage());
                }
            });
        });
    }
}
