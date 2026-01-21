package Views;

import Models.Bill;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PrintPane {
    private static final TextArea billFormat= new TextArea();
    private static final Button okBtn = new Button("OK");
    private Bill bill;

    public PrintPane()
    {}

    //Getters
    public TextArea getBillFormat() { return billFormat;}
    public Button getOkBtn() { return okBtn;}

    //Setter
    public void setBill(Bill bill) { this.bill = bill;}

    public BorderPane getPrintedBill()
    {
        BorderPane primaryPane = new BorderPane();
        primaryPane.setMaxSize(600, 500);
        primaryPane.setPadding(new Insets(10, 10, 10, 10));
        primaryPane.setStyle("-fx-background-color: #002d26; -fx-border-color: white");

        billFormat.setPrefColumnCount(100);
        billFormat.setPrefRowCount(100);
        billFormat.setWrapText(true);
        billFormat.setText(bill.printBill());

        Label lbl = new Label("Print Bill");
        lbl.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        lbl.setTextFill(Color.WHITE);

        HBox hbox = new HBox();
        hbox.getChildren().add(lbl);
        hbox.setAlignment(Pos.CENTER);

        // primaryPane.setTop(hbox);
        primaryPane.setCenter(billFormat);
        primaryPane.setBottom(okBtn);
        BorderPane.setAlignment(okBtn, Pos.CENTER_RIGHT);
        return primaryPane;
    }
}
