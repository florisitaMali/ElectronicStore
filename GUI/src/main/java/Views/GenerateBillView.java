package Views;

import Controller.GenerateBillController;
import DAO.ItemsDAO;
import Models.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GenerateBillView extends View {
    private GenerateBillController controller;

    private  BorderPane primaryPane = new BorderPane();
    private  Button addSoldItemBtn = new Button("ADD");
    private  Button deleteSoldItemBtn = new Button("DELETE");
    private  Button printBillBtn = new Button("PRINT");
    private final TableView<SoldItem> billTable = new TableView<>();
    private  TableColumn<SoldItem, String> nameColumn = new TableColumn<>("Item Name");
    private  TableColumn<SoldItem, Integer> quantityColumn = new TableColumn<>("Quantity");
    private  TableColumn<SoldItem, Double> priceColumn = new TableColumn<>("Price");
    private  TableColumn<SoldItem, Double> totalPriceColumn = new TableColumn<>("Total Price");
    private ObservableList<SoldItem> soldItems;
    private static TextField totalAmount = new TextField();
    private DeleteItemView deleteItemView;
    private ObservableList<Item> items = FXCollections.observableArrayList(ItemsDAO.getAllItems());
    private ListView<Item> itemList = new ListView<>();
    private Bill bill;

    // New input fields for adding an item
    private TextField itemNameTF = new TextField();
    private TextField itemQuantityTF = new TextField();

    public GenerateBillView(Employee emp){
        // Initialize the DeleteItemView
        setCurrentUser(emp);
        deleteItemView = new DeleteItemView();
        createNewBill();
        setupView();
    }

    // Getters
    public BorderPane getPrimaryPane() { return primaryPane; }
    public TextField getItemNameTF() { return itemNameTF; }
    public TextField getItemQuantityTF(){ return itemQuantityTF;}
    public Button getAddSoldItemBtn() { return addSoldItemBtn; }
    public Button getDeleteSoldItemBtn() { return deleteSoldItemBtn; }
    public Button getPrintBillBtn() { return printBillBtn; }
    public TableView<SoldItem> getBillTable() { return billTable; }
    public TableColumn<SoldItem, String> getNameColumn() { return nameColumn; }
    public TableColumn<SoldItem, Integer> getQuantityColumn() { return quantityColumn; }
    public TableColumn<SoldItem, Double> getPriceColumn() { return priceColumn; }
    public TableColumn<SoldItem, Double> getTotalPriceColumn() { return totalPriceColumn; }
    public ObservableList<SoldItem> getSoldItems() { return soldItems; }
    public TextField getTotalAmount() { return totalAmount; }
    public DeleteItemView getDeleteItemView() { return deleteItemView; }
    public ObservableList<Item> getItems() { return items; }
    public ListView<Item> getItemList() { return itemList; }
    public Bill getBill() { return bill; }
    public PrintPane pane = new PrintPane();
    @Override
    public Parent getView() {
        return primaryPane;
    }

    private BorderPane setupView() {
        // Style primary pane
        primaryPane.setPadding(new Insets(10, 10, 10, 10));
        primaryPane.setStyle("-fx-background-color: #002d26");

        //Table
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemName()));
        nameColumn.setPrefWidth(300);

        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("SoldQuantity"));
        quantityColumn.setPrefWidth(200);

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("SellingPrice"));
        priceColumn.setPrefWidth(200);

        totalPriceColumn.setCellValueFactory(cellData -> {
            Item item = cellData.getValue();
            return new SimpleObjectProperty<>(((SoldItem) item).getSoldQuantity() * item.getSellingPrice());
        });
        totalPriceColumn.setPrefWidth(200);
        billTable.getColumns().addAll(nameColumn, quantityColumn, priceColumn, totalPriceColumn);
        billTable.setMaxSize(900, 600);
        billTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        VBox pane = new VBox();
        if (soldItems != null){
            billTable.setItems(soldItems);
            updateTotalAmount();
        }

        totalAmount.setMaxWidth(900);
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().addAll(billTable, totalAmount);

        // Style top part
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        // Center the "Generate Bill" label in an HBox
        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER);
        Label lbl = new Label("Generate Bill");
        lbl.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        lbl.setTextFill(Color.WHITE);
        labelBox.getChildren().add(lbl);

        // New fields for adding an item
        GridPane gridPaneAddItem = new GridPane();
        gridPaneAddItem.setPadding(new Insets(10, 10, 10, 10));
        gridPaneAddItem.setVgap(10);
        gridPaneAddItem.setHgap(5);
        gridPaneAddItem.setAlignment(Pos.CENTER);

        Label itemName = new Label("Item Name");
        itemName.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
        itemName.setTextFill(Color.WHITE);
        gridPaneAddItem.add(itemName, 0, 0);
        GridPane.setValignment(itemName, VPos.TOP);
        String placeholderText = "Enter item name here ...";
        itemNameTF.setPromptText(placeholderText);

        itemList.setItems(items);
        itemList.setVisible(true);
        itemList.setPrefSize(300, 200);
        itemList.setPrefHeight(150);

        VBox v = new VBox(5);
        v.getChildren().addAll(itemNameTF, itemList);
        v.setAlignment(Pos.TOP_CENTER);
        gridPaneAddItem.add(v, 1, 0);

        Label itemQuantity = new Label("Item Quantity");
        itemQuantity.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
        itemQuantity.setTextFill(Color.WHITE);
        gridPaneAddItem.add(itemQuantity, 0, 1);

        // Style addItemButton
        addSoldItemBtn.setStyle("-fx-background-color: white;");
        addSoldItemBtn.setTextFill(Color.BLACK);

        gridPaneAddItem.add(itemQuantityTF, 1, 1);
        gridPaneAddItem.add(addSoldItemBtn, 1, 3);
        GridPane.setHalignment(addSoldItemBtn, HPos.RIGHT);

        vbox.getChildren().addAll(labelBox, gridPaneAddItem);

        // Style deleteItemButton
        deleteSoldItemBtn.setStyle("-fx-background-color: red;");
        deleteSoldItemBtn.setTextFill(Color.WHITE);

        // Create bottom part
        HBox hboxBottom = new HBox(20);
        hboxBottom.setPadding(new Insets(10, 10, 10, 10));
        hboxBottom.setAlignment(Pos.CENTER_RIGHT);
        hboxBottom.getChildren().addAll(deleteSoldItemBtn, printBillBtn);

        // Add all parts to the primary pane
        primaryPane.setTop(vbox);
        primaryPane.setBottom(hboxBottom);
        primaryPane.setCenter(pane);
        return primaryPane;
    }

    public void updateTotalAmount() {
        System.out.println(bill.getTotalPrice());
        totalAmount.setText(String.format("%.2f", bill.getTotalPrice()));
    }

    public void addSoldItems(SoldItem s) throws ItemNotFoundException {
        for (int i = 0; i < soldItems.size(); i++) {
            if (soldItems.get(i).getItemName().equals(s.getItemName())) {
                soldItems.get(i).setSoldQuantity(s.getSoldQuantity());
                refreshBillTable();
                return;
            }
        }
        // If the sold item does not exist in the list, just add it
        soldItems.add(s);
        bill.addSoldItems(s);
        refreshBillTable();
    }

    public void deleteSoldItem(SoldItem item) {
        for (SoldItem s : soldItems)
        {
            if(s.getItemName().equals(item.getItemName()))
            {
                soldItems.remove(s);
                bill.deleteSoldItem(s);
                billTable.setItems(soldItems);
                billTable.refresh();
                return;
            }
        }
        System.out.println("Not found.");
    }
    public void createNewBill() {
        bill = new Bill(getCurrentUser());
        soldItems = FXCollections.observableArrayList(bill.getSoldItems());
        billTable.setItems(FXCollections.observableArrayList(bill.getSoldItems()));
        totalAmount.clear();
        billTable.refresh();
    }
    public void refreshBillTable() {
        billTable.setItems(FXCollections.observableArrayList(bill.getSoldItems()));
        billTable.refresh();
        updateTotalAmount();
    }

}


