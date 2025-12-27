package Views;

import Controller.ManagerSectorsController;
import DAO.CategoryDAO;
import Models.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.net.PortUnreachableException;
import java.util.ArrayList;


public class ManagerSectorsView extends View {
    ManagerSectorsController controller;
    private final TableView<Item> inventoryTable = new TableView<>();
    private ArrayList<Item> itemList = new ArrayList<>();
    private final BorderPane mainLayout = new BorderPane();
    private final ObservableList<Sector> sectors;
    private final ComboBox<Sector> sectorComboBox;
    private final Button searchButton = new Button("Search");
    private final TextField searchField = new TextField();
    private final Button addButton = new Button("Add Item");
    private final Button editButton = new Button("Edit Item");
    private final Button deleteButton = new Button("Delete Item");
    private final Button addCategoryButton = new Button("Add Category");
    private final Button deleteCategoryButton = new Button("Delete Category");
    private final Button addSupplierButton = new Button("Add Supplier");
    private final TextField nameField = new TextField();

    // Getters
    public TableView<Item> getInventoryTable() {
        return inventoryTable;
    }
    public ArrayList<Item> getItemList() {
        return itemList;
    }
    public BorderPane getMainLayout() {
        return mainLayout;
    }
    public ObservableList<Sector> getSectors() {
        return sectors;
    }
    public ComboBox<Sector> getSectorComboBox() {
        return sectorComboBox;
    }
    public Button getSearchButton() {
        return searchButton;
    }
    public TextField getSearchField() {
        return searchField;
    }
    public Button getAddButton() {
        return addButton;
    }
    public Button getEditButton() {
        return editButton;
    }
    public Button getDeleteButton() {
        return deleteButton;
    }
    public Button getAddCategoryButton() {
        return addCategoryButton;
    }
    public void setController(ManagerSectorsController controller) {
        this.controller = controller;
    }
    public Button getAddSupplierButton() {
        return addSupplierButton;
    }
    public Button getDeleteCategoryButton(){
        return deleteCategoryButton;
    }
    public TextField getNameField() {
        return nameField;
    }

    public ManagerSectorsView(Employee emp)
    {
        sectors = FXCollections.observableArrayList(((Manager) getCurrentUser()).getSectors());
        sectorComboBox = new ComboBox<>(sectors);
        controller = new ManagerSectorsController(this);
        setView();
        setCurrentUser(emp);
    }

    @Override
    public Parent getView() {
        return mainLayout;
    }

    public void setView() {
        Label headerLabel = new Label("Manager Dashboard");
        headerLabel.setFont(new Font("Arial", 24));
        headerLabel.setTextFill(Color.WHITE);
        headerLabel.setStyle("-fx-background-color: #004D40; -fx-padding: 10px;");
        headerLabel.setAlignment(Pos.CENTER);

        // Table setup
        inventoryTable.setStyle("-fx-border-color: #e0f7ff; -fx-background-color: #f5f5f5;");

        TableColumn<Item, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemNameCol.setPrefWidth(200);

        TableColumn<Item, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(200);

        TableColumn<Item, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getItemCategory().toString())
        );
        categoryCol.setPrefWidth(200);

        TableColumn<Item, String> sectorCol = new TableColumn<>("Sector");
        sectorCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getItemCategory().getSector().toString())
        );
        sectorCol.setPrefWidth(200);


        TableColumn<Item, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getItemSupplier().getSupplierName())
        );
        supplierCol.setPrefWidth(200);

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
        inventoryTable.setMaxSize(1450, 600);
        inventoryTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Search field
        searchField.setPromptText("Search Items...");
        searchField.setStyle("-fx-border-color:#009688;");

        searchButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");

        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        addButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");
        editButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");
        addCategoryButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");
        deleteCategoryButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");
        addSupplierButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, addCategoryButton, deleteCategoryButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        HBox inputBox = new HBox(10, nameField, addSupplierButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10));

        VBox centerBox = new VBox(10, sectorComboBox, searchBox, inventoryTable, buttonBox, inputBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10));

        mainLayout.setTop(headerLabel);
        mainLayout.setCenter(centerBox);
        mainLayout.setStyle("-fx-background-color: #002d26;");
    }

    public void isLowStock()
    {
        for(Item i: itemList)
        {
            if(i.isOutOfStock())
                ShowAlert.showAlert("Item Out Of Stock",
                        i.getItemName() + " is below the stockLimit.\n"
                                + "Quantity: " + i.getQuantity() + "\n"
                                + "StockLimit: " + i.getStockLimit());
        }
    }

    public void loadData(Sector se) {
        itemList = CategoryDAO.getSectorsItems(se);
        inventoryTable.getItems().setAll(itemList);
        isLowStock();
    }
}