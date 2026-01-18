package Views;

import Models.Employee;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ChangePassword {
    private final TextField oldPassword = new TextField();
    private final TextField newPassword = new TextField();
    private final TextField confirmPassword = new TextField();

    public Dialog<String> changePassword(Employee emp)
    {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change your password");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the username label and text field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Old Password:"), 0, 0);
        grid.add(oldPassword, 1,0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPassword, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String oldPass = oldPassword.getText();
                String newPass = newPassword.getText();
                String confirmPass = confirmPassword.getText();
                if (!oldPass.equals(emp.getPassword())) {
                    ShowAlert.showAlert("Invalid password", "Your old password is not correct");
                } else if (newPass.equals(confirmPass)) {
                    return newPass;
                }
                ShowAlert.showAlert("Invalid password", "Ensure to correctly confirm your password.");
            }
            return null;
        });
        oldPassword.setId("oldPasswordField");
        newPassword.setId("passwordField");
        confirmPassword.setId("confirmPasswordField");
        return dialog;
    }
}
