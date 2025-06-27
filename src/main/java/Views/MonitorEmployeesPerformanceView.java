package Views;

import DAO.EmployeeDAO;
import DAO.BillDAO;
import Models.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.util.ArrayList;

public class MonitorEmployeesPerformanceView extends View{
    private final BorderPane mainLayout = new BorderPane();
    private final TableView<Employee> employeeTable = new TableView<>();
    private final DatePicker startDate = new DatePicker(LocalDate.now());
    private final DatePicker endDate = new DatePicker(LocalDate.now());
    private final TableColumn<Employee, Double> totalRevenue = new TableColumn<>("Total Revenue");
    private final Button searchButton = new Button("Search");
    private final TextField searchField = new TextField();

    public MonitorEmployeesPerformanceView(Employee emp)
    {
        setCurrentUser(emp);
        setView();
    }
    @Override
    public Parent getView()
    {
        return mainLayout;
    }

    public void setView()
    {
        //Create Table
        employeeTable.setStyle("-fx-border-color: #e0f7ff; -fx-background-color: #f5f5f5;");

        TableColumn<Employee, String> nameCol = new TableColumn<>("First Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<Employee, String> surnameCol = new TableColumn<>("Last Name");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        surnameCol.setPrefWidth(150);

        TableColumn<Employee, String> roleCol = new TableColumn<>("Sector");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("sector"));
        roleCol.setPrefWidth(150);

        TableColumn<Employee, Long> totalNrOfBillsCol = new TableColumn<>("Total Nr of Bills");
        totalNrOfBillsCol.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue();
            long nrOfBills = Bill.getBills(emp, startDate.getValue(), endDate.getValue()).size();
            return new ReadOnlyObjectWrapper<>(nrOfBills);
        });
        totalNrOfBillsCol.setPrefWidth(150);

        TableColumn<Employee, Long> nrOfItemsSold = new TableColumn<>("NrOfItemsSold");
        nrOfItemsSold.setCellValueFactory(cellData-> {
            Employee emp = cellData.getValue();
            long nrOfItems = getNrOfItems(Bill.getBills(emp, startDate.getValue(), endDate.getValue()));
            return new ReadOnlyObjectWrapper<>(nrOfItems);
        });
        nrOfItemsSold.setPrefWidth(150);

        totalRevenue.setCellValueFactory(cellData-> {
            Employee emp = cellData.getValue();
            double totalIncome = Statistics.getTotalIncome(startDate.getValue(), endDate.getValue(), Bill.getBills(emp, startDate.getValue(), endDate.getValue()));
            return new ReadOnlyObjectWrapper<>(totalIncome);
        });
        totalRevenue.setPrefWidth(150);

        TableColumn<Employee, Double> totalPercentageRevenue = new TableColumn<>("Percentage of totalRevenue");
        totalPercentageRevenue.setCellValueFactory(cellData-> {
            Employee emp = cellData.getValue();
            double persTotalRev = Statistics.getTotalIncome(startDate.getValue(), endDate.getValue(), Bill.getBills(emp, startDate.getValue(), endDate.getValue())) / Statistics.getTotalIncome(startDate.getValue(), endDate.getValue(), BillDAO.getAllBills()) * 100;
            return new ReadOnlyObjectWrapper<>(Double.parseDouble(String.format("%.2f", persTotalRev)));
        });
        totalPercentageRevenue.setPrefWidth(200);

        employeeTable.getColumns().addAll(nameCol, surnameCol, roleCol, totalNrOfBillsCol, nrOfItemsSold, totalRevenue, totalPercentageRevenue);
        employeeTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); // Allow columns to adjust dynamically
        employeeTable.setMaxSize(1100, 700);

        loadData();

        Label headerLabel = new Label("Manage Employees");
        headerLabel.setFont(new Font("Arial", 24));
        headerLabel.setTextFill(Color.WHITE);
        headerLabel.setStyle("-fx-background-color: #004D40; -fx-padding: 10px;");
        headerLabel.setAlignment(Pos.CENTER);

        //Search field
        searchField.setPromptText("Search Employees...");
        searchField.setStyle("-fx-border-color:#009688;");

        searchButton.setStyle("-fx-background-color:#009688; -fx-text-fill: white;");

        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        VBox centerBox = new VBox(10, searchBox, employeeTable);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10));

        mainLayout.setTop(headerLabel);
        mainLayout.setCenter(centerBox);
        mainLayout.setPadding(new Insets(10, 10, 10, 10));
        mainLayout.setStyle("-fx-background-color: #002d26;");
    }

    public void loadData() {
        ArrayList<Employee> employees = EmployeeDAO.getEmployeesOfSectors(((Manager)getCurrentUser()).getSectors());
        for(Sector s: ((Manager)getCurrentUser()).getSectors())
        {
            System.out.println(s);
        }
        employeeTable.getItems().setAll(employees);
    }

    public long getNrOfItems(ArrayList<Bill> bills)
    {
        ArrayList<SoldItem> soldItems = new ArrayList<>();
        for(Bill b: bills)
        {
            for(SoldItem s: b.getSoldItems())
                if (!soldItems.contains(s))
                    soldItems.add(s);

        }
        return soldItems.size();
    }
}
