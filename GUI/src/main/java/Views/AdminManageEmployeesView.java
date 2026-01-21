package Views;

import Controller.AdminManageEmployeesController;
import DAO.EmployeeDAO;
import Models.Cashier;
import Models.Employee;
import Models.Manager;
import Models.Sector;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Date;

public class AdminManageEmployeesView extends View{
    private final AdminManageEmployeesController controller;

    private final BorderPane mainLayout = new BorderPane();
    private final Button searchButton = new Button("Search");
    private final TextField searchField = new TextField();
    private final Button editButton = new Button("Edit Employee");
    private final Button addButton = new Button("Add Employee");
    private final Button deleteButton = new Button("Delete Employee");
    private final TableView<Employee> employeeTable = new TableView<>();;
    private final Button checkPermissions = new Button("CheckPermissions");

    public Button getEditButton() { return editButton;}
    public Button getAddButton() { return addButton;}
    public Button getDeleteButton() { return deleteButton;}
    public BorderPane getMainLayout() { return mainLayout; }
    public Button getSearchButton() { return searchButton;}
    public TextField getSearchField() { return searchField;}
    public TableView<Employee> getEmployeeTable() { return employeeTable;}

    public AdminManageEmployeesView(){
        controller = new AdminManageEmployeesController(this);
    }
    public AdminManageEmployeesView(Employee emp) {
        setCurrentUser(emp);
        setView();
        controller = new AdminManageEmployeesController(this);
    }

    @Override
    public Parent getView()
    {
        return mainLayout;
    }

    public void setView() {
        //Create Table
        employeeTable.setStyle("-fx-border-color: #e0f7ff; -fx-background-color: #f5f5f5;");

        TableColumn<Employee, String> nameCol = new TableColumn<>("First Name");
        //cellValueFactory responsible for providing the value that should be displayed in each cell of the column
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name")); // PropertyValueFactory return the property value of name
        nameCol.setPrefWidth(100);

        TableColumn<Employee, String> surnameCol = new TableColumn<>("Last Name");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        surnameCol.setPrefWidth(100);

        TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(100);

        TableColumn<Employee, String> username = new TableColumn<>("Username");
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        username.setPrefWidth(150);

        TableColumn<Employee, String> password= new TableColumn<>("Password");
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        password.setPrefWidth(150);

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Employee, Double> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryCol.setPrefWidth(100);

        TableColumn<Employee, Date> birthdayCol = new TableColumn<>("Date of Birth");
        birthdayCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        birthdayCol.setPrefWidth(100);

        TableColumn<Employee, String> phoneCol = new TableColumn<>("Phone Number");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setPrefWidth(100);

        TableColumn<Employee, String> sectorCol = new TableColumn<>("Sector");
        //cellData is responsible for providing the value that should be provided to that cell of table
        sectorCol.setCellValueFactory(cellData -> {
            StringBuilder sb = new StringBuilder(" ");
            if(cellData.getValue() instanceof Manager){
                for (Sector p : ((Manager)cellData.getValue()).getSectors()) {
                    if (p != null) {
                        sb.append(p.toString()).append("\n");
                    }
                }
            } else if(cellData.getValue() instanceof Cashier) {
                sb.append(((Cashier)cellData.getValue()).getSector());
            }
            return new SimpleStringProperty(sb.toString());
        });
        sectorCol.setPrefWidth(200);

        employeeTable.getColumns().addAll(nameCol, surnameCol, roleCol, username, password, birthdayCol, phoneCol, emailCol, salaryCol, sectorCol);
        employeeTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        employeeTable.setMaxSize(1300, 600);
        loadData();

        Label headerLabel = new Label("Manage Employees");
        headerLabel.setFont(new Font("Arial", 24));
        headerLabel.setTextFill(Color.WHITE);
        headerLabel.setStyle("-fx-background-color: #004D40; -fx-padding: 10px;");
        headerLabel.setAlignment(Pos.CENTER);

        // Search field
        searchField.setPromptText("Search Employees...");
        searchField.setStyle("-fx-border-color:#009688;");

        searchButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");

        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        addButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");
        editButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(10, searchBox, employeeTable, buttonBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10));

        mainLayout.setTop(headerLabel);
        mainLayout.setCenter(centerBox);
        mainLayout.setStyle("-fx-background-color: #002d26;");
    }

    public void loadData() {
        ArrayList<Employee> employees = EmployeeDAO.getEmployees(getCurrentUser());
        employeeTable.getItems().setAll(employees);
    }
}
