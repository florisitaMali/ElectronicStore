package Views;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class UsenameChange {
    private final TextField username = new TextField();

    public Dialog<String> changeUsername(String currentUsername)
    {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Username");
        dialog.setHeaderText("Change your username");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the username label and text field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("New Username");
        usernameField.setText(currentUsername);

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        //This makes possible to make the textField active without the need to click on the button
        usernameField.requestFocus();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return usernameField.getText();
            }
            return null;
        });
        return dialog;
    }
}
