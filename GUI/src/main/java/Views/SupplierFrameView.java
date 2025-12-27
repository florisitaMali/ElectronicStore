package Views;

import DAO.SuppliersDAO;
import Models.Employee;
import Models.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SupplierFrameView extends View{

    private final BorderPane supplierLayout = new BorderPane();
    private final TableView<Supplier> supplierTable = new TableView<>();
    private final Button updateButton = new Button("Update");
    private final Button addButton = new Button("Add Supplier");
    private final TextField nameField = new TextField();
    private final TextField addressField = new TextField();

    private final TableColumn<Supplier, String> supplierNameCol = new TableColumn<>("Supplier Name");
    private final TableColumn<Supplier, String> addressCol = new TableColumn<>("Address");
    private final TableColumn<Supplier, Void> actionCol = new TableColumn<>("Actions");

    public SupplierFrameView(Employee emp)
    {
        setCurrentUser(emp);
        suppliers = FXCollections.observableArrayList(SuppliersDAO.getAllSuppliers());
        loadData();
        setView();
    }

    private ObservableList<Supplier> suppliers;

    public BorderPane getSupplierLayout() {
        return supplierLayout;
    }

    public TableView<Supplier> getSupplierTable() {
        return supplierTable;
    }

    public Button getAddButton() {return addButton;}
    public TextField getNameField(){ return nameField;}
    public TextField getAddressField(){ return addressField;}
    public TableColumn<Supplier, String> getSupplierNameCol() { return supplierNameCol; }
    public TableColumn<Supplier, String> getAddressCol() { return addressCol; }
    public TableColumn<Supplier, Void> getActionCol() { return actionCol; }
    public Button getUpdateButton(){ return updateButton;}

    @Override
    public Parent getView()
    {
        return supplierLayout;
    }

    private void setView()
    {
        Label headerLabel = new Label("Supplier Management");
        headerLabel.setFont(new Font("Arial", 24));
        headerLabel.setTextFill(Color.WHITE);
        headerLabel.setStyle("-fx-background-color: #004D40; -fx-padding: 10px;");
        headerLabel.setAlignment(Pos.CENTER);

        supplierTable.setStyle("-fx-border-color: #e0f7ff; -fx-background-color: #f5f5f5;");
        supplierTable.setEditable(true);

        TableColumn<Supplier, String> supplierNameCol = new TableColumn<>("Supplier Name");
        supplierNameCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        supplierNameCol.setPrefWidth(200);

        TableColumn<Supplier, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.setCellFactory(TextFieldTableCell.forTableColumn());
        addressCol.setPrefWidth(300);

        TableColumn<Supplier, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150); // Set preferred width for the column
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("\u2716"); // ✖
            {
                deleteButton.setStyle("-fx-background-color: #f0f8e8; -fx-text-fill: black;");

                deleteButton.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(supplier);
                    SuppliersDAO.deleteSupplier(supplier);
                    System.out.println("Deleted Supplier: " + supplier.getSupplierName());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actionBox = new HBox(10, deleteButton);
                    actionBox.setAlignment(Pos.CENTER);
                    setGraphic(actionBox);
                }
            }
        });

        supplierTable.getColumns().addAll(supplierNameCol, addressCol, actionCol);
        supplierTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); // Allow columns to adjust dynamically
        supplierTable.setMaxSize(650, 600);

        nameField.setPromptText("Supplier Name");

        addressField.setPromptText("Address");

        addButton.setStyle("-fx-background-color: #009688; -fx-text-fill: white;");
        updateButton.setStyle("-fx-background-color: #009688; -fx-text-fill: white;");

        HBox inputBox = new HBox(10, nameField, addressField, addButton, updateButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10));

        VBox supplierCenterBox = new VBox(10, supplierTable, inputBox);
        supplierCenterBox.setAlignment(Pos.CENTER);
        supplierCenterBox.setPadding(new Insets(10));

        supplierLayout.setTop(headerLabel);
        supplierLayout.setCenter(supplierCenterBox);
        supplierLayout.setStyle("-fx-background-color:#002d26;");
    }

    private void loadData()
    {
        System.out.println(suppliers.size());
        supplierTable.getItems().addAll(suppliers);
    }
}
