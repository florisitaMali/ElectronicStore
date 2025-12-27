package Views;

import Controller.LoginController;
import Models.Role;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Objects;

public class LoginView {
    private final LoginController controller;
    private final StackPane primaryPane = new StackPane();
    private final Button loginBtn = new Button("Login");
    private final Button backBtn = new Button("Back");
    private final TextField userNameTextField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Role role;

    public LoginView(Role role) {
        this.role = role;
        System.out.println("Creating LoginView for role: " + role);  // Debugging statement
        controller = new LoginController(this);
        setView();
    }

    public Parent getView() {
        return primaryPane;
    }

    public Role getRole() {
        return role;
    }

    public Button getLoginBtn() {
        return loginBtn;
    }

    public Button getBackBtn()
    {
        return backBtn;
    }

    public TextField getUserNameTextField() {
        return userNameTextField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public void setView() {
        primaryPane.setAlignment(Pos.CENTER);
        primaryPane.setPadding(new Insets(5, 5, 5, 5));
        primaryPane.setStyle("-fx-border-color: black");

        GridPane mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10, 10, 20, 10));
        grid.setHgap(10);
        grid.setVgap(10);

        Text text = new Text("Login to your account");
        text.setFont(Font.font("Times new Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));
        text.setFill(Color.DARKBLUE);
        GridPane.setHalignment(text, HPos.CENTER);
        mainGrid.add(text, 0, 0);

        Label label = new Label("Username");
        label.setFont(Font.font("Times new Roman", FontWeight.MEDIUM, FontPosture.REGULAR, 15));
        grid.add(label, 0, 0);
        grid.add(userNameTextField, 1, 0);

        label = new Label("Password");
        label.setFont(Font.font("Times new Roman", FontWeight.MEDIUM, FontPosture.REGULAR, 15));
        grid.add(label, 0, 1);
        grid.add(passwordField, 1, 1);

        loginBtn.setStyle("-fx-background-color: #" + toHexCode() + "; "
                + "-fx-text-fill: white; "
                + "-fx-font-family: 'Times new Roman';"
                + "-fx-font-size: 15px");
        backBtn.setStyle("-fx-background-color: #" + toHexCode() + "; "
                + "-fx-text-fill: white; "
                + "-fx-font-family: 'Times new Roman';"
                + "-fx-font-size: 15px");
        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(backBtn, loginBtn);
        grid.add(hBox, 1, 2);

        GridPane.setHalignment(hBox, HPos.RIGHT);

        mainGrid.add(grid, 0, 1);

        ImageView whiteScreen = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/images/whiteScreen.png")).toExternalForm()));
        whiteScreen.setFitWidth(320);
        whiteScreen.setFitHeight(200);

        ImageView backgroundImage = new ImageView(new Image(getClass().getResource("/images/electronicStoreBackgroundPhoto.jpg").toExternalForm()));
        backgroundImage.fitHeightProperty().bind(primaryPane.heightProperty());
        backgroundImage.fitWidthProperty().bind(primaryPane.widthProperty());

        primaryPane.getChildren().addAll(backgroundImage,whiteScreen, mainGrid);
    }

    private String toHexCode() {
        return String.format("%02X%02X%02X",
                (int) (Color.DARKBLUE.getRed() * 255),
                (int) (Color.DARKBLUE.getGreen() * 255),
                (int) (Color.DARKBLUE.getBlue() * 255));
    }
}
