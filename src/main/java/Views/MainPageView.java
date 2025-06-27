package Views;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MainPageView extends View{
    private final StackPane primaryPane = new StackPane();
    private final Button cashier = new Button();
    private final Button manager = new Button();
    private final Button admin = new Button();

    public MainPageView(){
        setView();
    }

    public StackPane getPrimaryPane() {
        return primaryPane;
    }

    public Button getCashier() {
        return cashier;
    }

    // Getter for manager button
    public Button getManager() {
        return manager;
    }

    // Getter for admin button
    public Button getAdmin() {
        return admin;
    }

    public Parent getView()
    {
        return primaryPane;
    }

    private void setView()
    {
        primaryPane.setAlignment(Pos.CENTER);
        primaryPane.setPadding(new Insets(5,5,5,5));
        primaryPane.setStyle("-fx-border-color: black");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10, 10, 20, 10));
        grid.setHgap(10);
        grid.setVgap(20);

        Text text = new Text("Choose your position");
        text.setFont(Font.font("Times new Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));
        grid.add(text, 0, 0);
        GridPane.setHalignment(text, HPos.CENTER);

        // Create Cashier BUTTON
        GridPane cashierContent = new GridPane();
        cashierContent.setStyle("-fx-alignment: CENTER");
        cashierContent.setVgap(10);

        Text role = new Text("Cashier");
        role.setFont(Font.font("Times new Roman", FontWeight.BOLD, FontPosture.REGULAR, 10));
        cashierContent.add(role, 0, 0);
        GridPane.setHalignment(role, HPos.CENTER);

        ImageView img = new ImageView(new Image(getClass().getResource("/images/cashier.png").toExternalForm()));
        img.setFitHeight(90);
        img.setFitWidth(90);
        cashierContent.add(img, 0, 1);
        GridPane.setHalignment(img, HPos.CENTER);

        cashier.setGraphic(cashierContent);
        cashier.setStyle("-fx-background-color: White; -fx-border-color: black; -fx-border-width: 3px");
        cashier.setMinHeight(150);
        cashier.setMinWidth(110);

        // Create Manager BUTTON
        GridPane managerContent = new GridPane();
        managerContent.setStyle("-fx-alignment: CENTER");
        managerContent.setVgap(18);

        role = new Text("Manager");
        role.setFont(Font.font("Times new Roman", FontWeight.BOLD, FontPosture.REGULAR, 10));
        managerContent.add(role, 0, 0);
        GridPane.setHalignment(role, HPos.CENTER);

        img = new ImageView(new Image(getClass().getResource("/images/manager.png").toExternalForm()));
        img.setFitHeight(82);
        img.setFitWidth(90);
        managerContent.add(img, 0, 1);
        GridPane.setHalignment(img, HPos.CENTER);

        manager.setGraphic(managerContent);
        manager.setStyle("-fx-background-color: White; -fx-border-color: black; -fx-border-width: 3px");
        manager.setMinHeight(150);
        manager.setMinWidth(110);

        // Create Administrator BUTTON
        GridPane adminContent = new GridPane();
        adminContent.setStyle("-fx-alignment: CENTER");
        adminContent.setVgap(20);

        role = new Text("Administrator");
        role.setFont(Font.font("Times new Roman", FontWeight.BOLD, FontPosture.REGULAR, 10));
        adminContent.add(role, 0, 0);
        GridPane.setHalignment(role, HPos.CENTER);

        img = new ImageView(new Image(getClass().getResource("/images/admin.png").toExternalForm()));
        img.setFitHeight(80);
        img.setFitWidth(90);
        adminContent.add(img, 0, 1);
        GridPane.setHalignment(img, HPos.CENTER);

        admin.setGraphic(adminContent);
        admin.setStyle("-fx-background-color: White; -fx-border-color: black; -fx-border-width: 3px");
        admin.setMinHeight(150);
        admin.setMinWidth(110);

        HBox hbox = new HBox(15);
        hbox.getChildren().add(cashier);
        hbox.getChildren().add(manager);
        hbox.getChildren().add(admin);
        grid.add(hbox, 0, 1);

        ImageView backgroundImage = new ImageView(new Image(getClass().getResource("/images/electronicStoreBackgroundPhoto.jpg").toExternalForm()));
        ImageView whiteScreen = new ImageView(new Image(getClass().getResource("/images/whiteScreen.png").toExternalForm()));
        whiteScreen.setFitWidth(400);
        whiteScreen.setFitHeight(220);
        whiteScreen.setOpacity(0.7);

        backgroundImage.fitHeightProperty().bind(primaryPane.heightProperty());
        backgroundImage.fitWidthProperty().bind(primaryPane.widthProperty());
        primaryPane.getChildren().add(backgroundImage);
        primaryPane.getChildren().add(whiteScreen);
        primaryPane.getChildren().add(grid);
    }
}
