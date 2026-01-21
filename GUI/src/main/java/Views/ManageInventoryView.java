package Views;

import Controller.ManageInventoryController;
import DAO.ItemsDAO;
import Models.Employee;
import Models.Item;
import Models.Supplier;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageInventoryView extends View {
    private final ManageInventoryController controller;

    private final BorderPane mainLayout = new BorderPane();
    private final TextField searchField = new TextField();
    private final Button searchButton = new Button("Search");
    private final Button addButton = new Button("Add Item");
    private final Button editButton = new Button("Edit Item");
    private final Button deleteButton = new Button("Delete Item");
    private TableView<Item> inventoryTable;
    private ArrayList<Item> itemList = new ArrayList<>();
    private final Button addSupplierButton = new Button("Add Supplier");
    private final TextField nameField = new TextField();

    public ManageInventoryView(Employee emp)
    {
        setCurrentUser(emp);
        controller = new ManageInventoryController(this);
        setView();
    }

    public BorderPane getMainLayout() { return mainLayout; }
    public TextField getSearchField() { return searchField; }
    public Button getSearchButton() { return searchButton; }
    public Button getAddButton() { return addButton; }
    public Button getEditButton() { return editButton; }
    public Button getDeleteButton() { return deleteButton; }
    public TableView<Item> getInventoryTable() { return inventoryTable; }
    public ArrayList<Item> getItemList() { return itemList; }
    public Button getAddSupplierButton() { return addSupplierButton; }
    public TextField getNameField() { return nameField; }

    @Override
    public Parent getView()
    {
        return mainLayout;
    }

    private void setView() {
        Label headerLabel = new Label("Administrator Dashboard");
        headerLabel.setFont(new Font("Arial", 24));
        headerLabel.setTextFill(Color.WHITE);
        headerLabel.setStyle("-fx-background-color: #004D40; -fx-padding: 10px;");
        headerLabel.setAlignment(Pos.CENTER);

        // Table setup
        inventoryTable = new TableView<>();
        inventoryTable.setStyle("-fx-border-color: #e0f7ff; -fx-background-color: #f5f5f5;");

        TableColumn<Item, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemNameCol.setPrefWidth(200);

        TableColumn<Item, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(200);

        TableColumn<Item, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getItemCategory().getName())
        );
        categoryCol.setPrefWidth(150);

        TableColumn<Item, String> sectorCol = new TableColumn<>("Sector");
        sectorCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getItemCategory().getSector().toString())
        );
        sectorCol.setPrefWidth(150);

        TableColumn<Item, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(cellData -> cellData.getValue().getItemSupplier().supplierNameProperty());
        supplierCol.setPrefWidth(150);

        TableColumn<Item, Double> purchasedPriceCol = new TableColumn<>("Purchased Price");
        purchasedPriceCol.setCellValueFactory(new PropertyValueFactory<>("purchasedPrice"));
        purchasedPriceCol.setPrefWidth(150);

        TableColumn<Item, Double> sellingPriceCol = new TableColumn<>("Selling Price");
        sellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        sellingPriceCol.setPrefWidth(150);

        TableColumn<Item, Long> stockLimit = new TableColumn<>("Stock Limit");
        stockLimit.setCellValueFactory(new PropertyValueFactory<>("stockLimit"));
        stockLimit.setPrefWidth(150);

        inventoryTable.getColumns().addAll(itemNameCol, quantityCol, categoryCol, sectorCol, supplierCol,
                purchasedPriceCol, sellingPriceCol, stockLimit);

        loadData();
        inventoryTable.setMaxSize(1200, 800);
        inventoryTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); // Allow columns to adjust dynamically


        //Search field
        searchField.setPromptText("Search Items...");
        searchField.setStyle("-fx-border-color:#009688;");

        searchButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");

        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        // Buttons for Add, Edit, Delete
        HBox buttonBox = getHBox();

        VBox centerBox = new VBox(10, searchBox, inventoryTable, buttonBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10));

        addButton.setStyle("-fx-background-color: #009688; -fx-text-fill: white;");

        HBox inputBox = new HBox(10, nameField, addSupplierButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10));

        addSupplierButton.setStyle("-fx-background-color: #009688; -fx-text-fill: white;");

        mainLayout.setTop(headerLabel);
        mainLayout.setCenter(centerBox);
        mainLayout.setBottom(inputBox);
        mainLayout.setStyle("-fx-background-color: #002d26;");
    }

    private HBox getHBox() {
        addButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");

        editButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");

        deleteButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        return buttonBox;
    }

    private void loadData() {
        itemList = ItemsDAO.getAllItems();
        inventoryTable.getItems().setAll(itemList);
    }
}