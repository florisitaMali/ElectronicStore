package Views;

import Controller.CostIncomeController;
import Models.Employee;
import Models.Statistics;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TotalCostIncomeView extends View {
    private final CostIncomeController controller;
    private final Button calculateButton = new Button("Calculate");
    private final DatePicker endDatePicker = new DatePicker();
    private final DatePicker startDatePicker = new DatePicker();
    private final Label totalIncomeLabel = new Label();
    private final Label totalCostLabel = new Label();
    private final StackPane root = new StackPane();
    private final TableView<Statistics> statisticsTableView = new TableView<>();

    // Getters for the private fields
    public Button getCalculateButton() {
        return calculateButton;
    }

    public DatePicker getStartDatePicker() {
        return startDatePicker;
    }

    public DatePicker getEndDatePicker() {
        return endDatePicker;
    }

    public Label getTotalIncomeLabel() {
        return totalIncomeLabel;
    }

    public Label getTotalCostLabel() {
        return totalCostLabel;
    }

    public StackPane getRoot() {
        return root;
    }

    public TableView<Statistics> getStatisticsTableView() {
        return statisticsTableView;
    }

    public TotalCostIncomeView(Employee emp) {
        controller = new CostIncomeController(this);
        setCurrentUser(emp);
        setView();
    }

    @Override
    public Parent getView() {
        return root;
    }

    private void setView() {
        Label tittle = new Label("Cost / Income / Revenue");
        HBox hBox = new HBox(tittle);
        tittle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        hBox.setStyle("-fx-background-color:white");
        hBox.setAlignment(Pos.CENTER);
        hBox.setMaxSize(850, 100);

        Label startDateLabel = new Label("Start Date (YYYY-MM-DD):");
        startDateLabel.setTextFill(Color.WHITE);
        Label endDateLabel = new Label("End Date (YYYY-MM-DD):");
        endDateLabel.setTextFill(Color.WHITE);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(startDateLabel, 0, 0);
        gridPane.add(startDatePicker, 1, 0);
        gridPane.add(endDateLabel, 0, 1);
        gridPane.add(endDatePicker, 1, 1);
        gridPane.add(calculateButton, 0, 2, 2, 1);
        gridPane.setAlignment(Pos.CENTER);

        //Set up the statistics table with fixed preferred widths
        TableColumn<Statistics, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().getDate() != null
                ? new ReadOnlyStringWrapper(cellData.getValue().getDate().toString())
                : new ReadOnlyStringWrapper("Total"));
        dateCol.setPrefWidth(100);

        TableColumn<Statistics, Double> revenueCol = new TableColumn<>("Total Revenue");
        revenueCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        revenueCol.setPrefWidth(150);

        TableColumn<Statistics, Double> itemCost = new TableColumn<>("Purchased Item Cost");
        itemCost.setCellValueFactory(new PropertyValueFactory<>("totalItemCost"));
        itemCost.setPrefWidth(150);

        TableColumn<Statistics, Double> wagesCost = new TableColumn<>("Wages Cost");
        wagesCost.setCellValueFactory(new PropertyValueFactory<>("totalWagesCost"));
        wagesCost.setPrefWidth(150);

        TableColumn<Statistics, Double> incomeCol = new TableColumn<>("Total Income");
        incomeCol.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
        incomeCol.setPrefWidth(150);

        TableColumn<Statistics, Integer> nrOfBillsCol = new TableColumn<>("Number of Bills");
        nrOfBillsCol.setCellValueFactory(new PropertyValueFactory<>("nrOfBills"));
        nrOfBillsCol.setPrefWidth(150);

        statisticsTableView.getColumns().addAll(dateCol, wagesCost, itemCost, incomeCol, nrOfBillsCol, revenueCol);
        statisticsTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); // Allow columns to adjust dynamically
        statisticsTableView.setMaxSize(850, 600);
        VBox tableBox = new VBox(statisticsTableView);
        tableBox.setAlignment(Pos.CENTER);
        tableBox.setPadding(new Insets(10));

        VBox centerBox = new VBox(10, hBox, gridPane, tableBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        root.setStyle("-fx-background-color: #002d26");
        root.getChildren().add(centerBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
    }
}
