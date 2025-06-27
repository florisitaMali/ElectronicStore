package Views;

import Controller.ReadBillController;
import Models.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class ReadBillsView extends View{
    private ReadBillController readBillController;

    private BorderPane primaryPane = new BorderPane();
    private ReadBillTableView readBillTableView;

    //Getters
    public ReadBillTableView getReadBillTableView() { return readBillTableView;}

    public ReadBillsView(Employee emp){
        setCurrentUser(emp);
        readBillTableView = new ReadBillTableView(this);
        setView();
        readBillController = new ReadBillController(this);
    }

    @Override
    public Parent getView()
    {
        return primaryPane;
    }

    public void setView()
    {
        primaryPane.setPadding(new Insets(10,10,10,10));
        primaryPane.setStyle("-fx-background-color: #002d26");

        // Style top part
        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER);
        Label lbl = new Label("Read Bill");
        lbl.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        lbl.setTextFill(Color.WHITE);
        labelBox.getChildren().add(lbl);

        //Set Top part to primaryPane
        primaryPane.setTop(labelBox);

        //Set table in the center
        setCenter(readBillTableView.getBillTableChild());
    }

    public void setCenter(Pane pane)
    {
        primaryPane.setCenter(pane);
    }
}
