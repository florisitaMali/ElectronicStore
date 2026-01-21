package Controller;

import DAO.BillDAO;
import DAO.ItemsDAO;
import Models.Statistics;
import Views.MonitorStatisticsView;
import javafx.scene.chart.PieChart;

import java.time.LocalDate;

public class MonitorStatisticsController {
    private final MonitorStatisticsView view;

    public MonitorStatisticsController(MonitorStatisticsView view)
    {
        this.view = view;
        enableButtons();
    }

    private void enableButtons()
    {
        updateStatistics(LocalDate.now(), LocalDate.now());
        setStartDatePickerAction();
        setEndDatePickerAction();
    }

    private void setStartDatePickerAction(){
        view.getStartDatePicker().setOnAction(event -> updateStatistics(view.getStartDatePicker().getValue(), view.getEndDatePicker().getValue()));
    }
    private void setEndDatePickerAction(){
        view.getEndDatePicker().setOnAction(event -> updateStatistics(view.getStartDatePicker().getValue(), view.getEndDatePicker().getValue()));
    }

    // Updates the statistics based on the selected date
    private void updateStatistics(LocalDate startdate, LocalDate enddate) {
        view.getStatsSummary().setText(getStatisticsSummary(startdate, enddate));

        view.getItemsSoldChart().getData().clear();
        BillDAO.getItemsSoldStatistics(startdate, enddate).forEach((itemName, value) ->
                view.getItemsSoldChart().getData().add(new PieChart.Data(itemName, value)));

        view.getItemsPurchasedChart().getData().clear();
        ItemsDAO.getItemsPurchasedStatistics(startdate, enddate).forEach((itemName, value) ->
                view.getItemsPurchasedChart().getData().add(new PieChart.Data(itemName, value)));

        view.getItemsSoldChart().setTitle("Items Sold Statistics");
        view.getItemsPurchasedChart().setTitle("Items Purchased Statistics");

        // Styling for the charts
        view.getItemsSoldChart().setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        view.getItemsPurchasedChart().setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
    }

    // Generates statistics summary for the selected date
    private String getStatisticsSummary(LocalDate startdate, LocalDate endDate) {
        String summary =   "Total Number of Bills: " + BillDAO.getAllBills(startdate, endDate).size() + "\n" +
                "Total Income (Selling): $" + Statistics.getTotalIncome(startdate, endDate, BillDAO.getAllBills(startdate, endDate))+ "\n" +
                "Total Cost (Buying): $" + Statistics.getTotalCostOfPurchasingItem(startdate, endDate);
        return summary;
    }
}
