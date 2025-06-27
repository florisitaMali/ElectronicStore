package Views;

import Models.SoldItem;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class DeleteItemView {
    private static final BorderPane borderPane= new BorderPane();
    private static ArrayList<CheckBox> soldItemCheckBox= new ArrayList<>();
    private static final Button cancelBtn = new Button("Cancel");
    private static final Button approveBtn = new Button("Approve");

    public ArrayList<CheckBox> getSoldItemCheckBox() { return soldItemCheckBox; }
    public Button getCancelBtn() { return cancelBtn; }
    public Button getApproveBtn() { return approveBtn; }

    public BorderPane deleteSoldItem(GenerateBillView view) {
        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setTop(new Label("Delete item:"));
        borderPane.setStyle("-fx-background-color: white");

        ObservableList<SoldItem> soldItems = view.getSoldItems();
        for (SoldItem s : soldItems) {
            System.out.println(s.getItemName() + " " + s.getQuantity());
        }
        VBox vbox = new VBox(5);
        updateCheckBoxes(soldItems);
        vbox.getChildren().addAll(soldItemCheckBox);
        vbox.setMaxSize(900, 600);

        borderPane.setCenter(vbox);
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(cancelBtn, approveBtn);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        borderPane.setBottom(hbox);
        borderPane.setMaxSize(900, 600);
        return borderPane;
    }

    public void updateCheckBoxes(ObservableList<SoldItem> soldItems) {
        soldItemCheckBox.clear();
        for (SoldItem item : soldItems) {
            soldItemCheckBox.add(new CheckBox(item.getItemName()));
        }
    }
}

