package Views;

import Controller.ProfileController;
import Controller.ReadBillController;
import Models.Cashier;
import Models.Employee;
import Models.Manager;
import Models.Permission;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;

public class Profile extends View {
    private final ProfileController controller;
    private final BorderPane mainPane = new BorderPane();
    private final Button personalDetails = new Button("Personal Details");
    private final Button workRelatedDetails = new Button("Work Related Details");
    private final Button UsernamePassWord = new Button("Username & Password");
    private final Button changeUsername = new Button("Change Username");
    private final Button changePassword = new Button("Change Password");
    private final GridPane personalInfo = new GridPane();
    private final GridPane otherInfo = new GridPane();
    private final GridPane securityInfo = new GridPane();
    private final TextField username = new TextField();

    public Profile(Employee emp) {
        setCurrentUser(emp);
        controller = new ProfileController(this);
        setView();
    }

    public BorderPane getMainPane() {
        return mainPane;
    }

    public Button getPersonalDetails() {
        return personalDetails;
    }

    public Button getWorkRelatedDetails() {
        return workRelatedDetails;
    }

    public Button getUsernamePassWord() {
        return UsernamePassWord;
    }

    public GridPane getPersonalInfo() {
        return personalInfo;
    }

    public GridPane getOtherInfo() {
        return otherInfo;
    }

    public GridPane getSecurityInfo() {
        return securityInfo;
    }

    public Button getChangeUsername(){ return changeUsername;}
    public Button getChangePassword(){ return changePassword;}
    public TextField getUsername(){ return username;}

    @Override
    public Parent getView() {
        return mainPane;
    }

    private void setView() {
        personalInfo.add(createLabel("Personal Details"), 0, 0, 3, 1); // 3 and 1 are the number of columns and rows that the label will span
        personalInfo.add(new Separator(), 0, 1, 3, 1);//same as that above
        personalInfo.add(createLabel("Name:"), 0, 2);
        personalInfo.add(new Separator(Orientation.VERTICAL), 1, 2);
        personalInfo.add(createLabel(getCurrentUser().getName()), 2, 2);
        personalInfo.add(createLabel("Surname:"), 0, 3);
        personalInfo.add(new Separator(Orientation.VERTICAL), 1, 3);
        personalInfo.add(createLabel(getCurrentUser().getSurname()), 2, 3);
        personalInfo.add(createLabel("Date of Birth:"), 0, 4);
        personalInfo.add(new Separator(Orientation.VERTICAL), 1, 4);
        personalInfo.add(createLabel(DateTimeFormatter.ofPattern("dd/MM/YYYY").format(getCurrentUser().getDateOfBirth())), 2, 4);
        personalInfo.add(createLabel("Email:"), 0, 5);
        personalInfo.add(new Separator(Orientation.VERTICAL), 1, 5);
        personalInfo.add(createLabel(getCurrentUser().getEmail()), 2, 5);
        personalInfo.add(createLabel("Phone Number:"), 0, 6);
        personalInfo.add(new Separator(Orientation.VERTICAL), 1, 6);
        personalInfo.add(createLabel(getCurrentUser().getPhoneNumber()), 2, 6);
        personalInfo.setStyle("-fx-background-color: white");
        personalInfo.setVgap(20);
        personalInfo.setHgap(20);
        personalInfo.setMaxSize(1000, 600);
        personalInfo.setAlignment(Pos.CENTER);

        Label workRelated = createLabel("Other Information");
        otherInfo.add(workRelated, 0, 0, 3, 1);
        otherInfo.add(new Separator(), 0, 1, 3, 1);
        otherInfo.add(createLabel("Role:"), 0, 2);
        otherInfo.add(new Separator(Orientation.VERTICAL), 1, 2);
        otherInfo.add(createLabel(getCurrentUser().getRole().toString()), 2, 2);
        otherInfo.add(createLabel("Salary:"), 0, 3);
        otherInfo.add(new Separator(Orientation.VERTICAL), 1, 3);
        otherInfo.add(createLabel(String.valueOf(getCurrentUser().getSalary())), 2, 3);
        otherInfo.setStyle("-fx-background-color: white");
        otherInfo.setVgap(20);
        otherInfo.setVgap(20);
        otherInfo.setMaxSize(1000, 600);
        otherInfo.setAlignment(Pos.CENTER);

        changeUsername.setId("changeUsernameBtn");
        changePassword.setId("changePasswordBtn");

        if (getCurrentUser() instanceof Cashier) {
            otherInfo.add(createLabel("Sector:"), 0, 4);
            otherInfo.add(new Separator(Orientation.VERTICAL), 1, 4);
            otherInfo.add(createLabel(((Cashier) getCurrentUser()).getSector().toString()), 2, 4);
        } else if (getCurrentUser() instanceof Manager) {
            otherInfo.add(createLabel("Sector:"), 0, 4);
            otherInfo.add(new Separator(Orientation.VERTICAL), 1, 4);
            String sectors = "";
            for (int i = 0; i < ((Manager) getCurrentUser()).getSectors().size(); i++)
                sectors = sectors + ((Manager) getCurrentUser()).getSectors().get(i).toString() + "\n";
            otherInfo.add(createLabel(sectors), 2, 4);
        }
        otherInfo.add(createLabel("Access Level:"), 0, 5);
        otherInfo.add(new Separator(Orientation.VERTICAL), 1, 5);
        StringBuilder sb = new StringBuilder();
        for (Permission p : getCurrentUser().getAccessLevel()) {
            sb.append(p).append("\n");
        }
        otherInfo.add(createLabel(sb.toString()), 2, 5);

        Label securityInfoLbl = createLabel("Username & Password");
        securityInfo.add(securityInfoLbl, 0, 0, 3, 1);
        securityInfo.add(new Separator(), 0, 1, 3, 1);
        securityInfo.add(createLabel("Username:"), 0, 2);
        securityInfo.add(new Separator(Orientation.VERTICAL), 1, 2);
        username.setText(getCurrentUser().getUsername());
        username.setEditable(false);
        securityInfo.add(username, 2, 2);
        securityInfo.add(new Separator(Orientation.VERTICAL), 2, 2);
        securityInfo.add(changeUsername, 4, 2);
        securityInfo.add(createLabel("Password:"), 0, 3);
        securityInfo.add(new Separator(Orientation.VERTICAL), 1, 3);
        securityInfo.add(createLabel("********"), 2, 3);
        securityInfo.add(new Separator(Orientation.VERTICAL), 3, 3);
        securityInfo.add(changePassword, 4, 3);

        securityInfo.setStyle("-fx-background-color: White");
        securityInfo.setMaxSize(1000, 600);
        securityInfo.setHgap(20);
        securityInfo.setVgap(20);
        securityInfo.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(30);
        personalDetails.setStyle("-fx-background-color: white; -fx-border-color: black");
        personalDetails.setPrefSize(300, 40);
        personalDetails.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));

        workRelatedDetails.setStyle("-fx-background-color: white; -fx-border-color: black");
        workRelatedDetails.setPrefSize(300, 40);
        workRelatedDetails.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));

        UsernamePassWord.setStyle("-fx-background-color: white; -fx-border-color: black");
        UsernamePassWord.setPrefSize(300, 40);
        UsernamePassWord.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));

        vBox.getChildren().addAll(personalDetails, workRelatedDetails, UsernamePassWord);
        vBox.setAlignment(Pos.CENTER);
        mainPane.setStyle("-fx-background-color: #002d26");
        mainPane.setLeft(vBox);
        mainPane.setCenter(personalInfo);
    }

    private Label createLabel(String s)
    {
        Label label = new Label(s);
        label.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
        return label;
    }
}
