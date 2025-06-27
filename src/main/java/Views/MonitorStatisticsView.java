package Views;

import Controller.MonitorStatisticsController;
import Models.Employee;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;

public class MonitorStatisticsView extends View{
    private final MonitorStatisticsController controller;
    private final VBox mainContent = new VBox(20);
    private final PieChart itemsSoldChart = new PieChart();
    private final PieChart itemsPurchasedChart = new PieChart();
    private final DatePicker startDatePicker = new DatePicker(LocalDate.now());
    private final DatePicker endDatePicker = new DatePicker(LocalDate.now());
    private final Label statsSummary = new Label();

    public VBox getMainContent() {
        return mainContent;
    }

    public PieChart getItemsSoldChart() {
        return itemsSoldChart;
    }

    public PieChart getItemsPurchasedChart() {
        return itemsPurchasedChart;
    }

    public DatePicker getStartDatePicker() {
        return startDatePicker;
    }

    public DatePicker getEndDatePicker() {
        return endDatePicker;
    }

    public Label getStatsSummary() {
        return statsSummary;
    }

    public MonitorStatisticsView(Employee emp)
    {
        controller = new MonitorStatisticsController(this);
        setCurrentUser(emp);
        setView();
    }

    @Override
    public Parent getView()
    {
        return mainContent;
    }

    private void setView()
    {
        Label title = new Label("Monitor Performance");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        StackPane titlePane = new StackPane(title);
        titlePane.setStyle("-fx-padding: 10; -fx-background-color: #2e4e3e;");

        // DatePicker for Date Selection
        startDatePicker.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
        endDatePicker.setStyle("-fx-font-size: 16px; -fx-padding: 10;");

        // Performance Monitoring Summary Section
        statsSummary.setFont(Font.font("Arial", 16));
        statsSummary.setTextFill(Color.BLACK);
        VBox summaryBox = new VBox(statsSummary);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-alignment: top-left;");

        ScrollPane scrollPane = new ScrollPane(summaryBox);
        scrollPane.setMaxWidth(1000);
        scrollPane.setStyle("-fx-background: white; -fx-padding: 10;");

        // Pie Charts for Items Sold and Purchased
        HBox chartsBox = new HBox(20, itemsPurchasedChart, itemsSoldChart);
        chartsBox.setAlignment(Pos.CENTER);
        chartsBox.setMaxWidth(1000);
        chartsBox.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-alignment: top-left;");

        HBox hBox = new HBox(20, startDatePicker, endDatePicker);
        hBox.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll( hBox, scrollPane, chartsBox);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setStyle("-fx-background-color: #002d26");
        // Main Layout
        BorderPane layout = new BorderPane();
        layout.setTop(titlePane);
        layout.setCenter(mainContent);
    }
}
