package Controller;

import DAO.*;
import Models.Bill;
import Models.Statistics;
import Views.TotalCostIncomeView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CostIncomeController {
    private final TotalCostIncomeView view;

    public CostIncomeController(TotalCostIncomeView view) {
        this.view = view;
        initialize();
    }

    private void initialize() {
        view.getCalculateButton().setOnAction(e -> calculateStatistics());
    }

    private void calculateStatistics() {
        LocalDate startDate = view.getStartDatePicker().getValue();
        LocalDate endDate = view.getEndDatePicker().getValue();

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return;
        }

        List<Statistics> statisticsList = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            ArrayList<Bill> bills = BillDAO.getAllBills(currentDate, currentDate);
            double totalIncome = Statistics.getTotalIncome(currentDate, currentDate, bills);
            double totalItemCost = Statistics.getTotalCostOfPurchasingItem(currentDate, currentDate);
            double totalWagesCost;
            if(currentDate.getDayOfMonth() == 1) {
                totalWagesCost = Statistics.getTotalCostOfSalary();
            } else
                totalWagesCost = 0;
            int nrOfBills = bills.size();
            double totalRevenue = totalIncome - (totalWagesCost + totalItemCost);

            statisticsList.add(new Statistics(currentDate, totalItemCost, totalWagesCost, totalIncome, totalRevenue, nrOfBills));

            currentDate = currentDate.plusDays(1);
        }

        // Compute total statistics
        double totalRevenue = statisticsList.stream().mapToDouble(Statistics::getTotalRevenue).sum();
        double totalIncome = statisticsList.stream().mapToDouble(Statistics::getTotalIncome).sum();
        double totalItemCost = statisticsList.stream().mapToDouble(Statistics::getTotalItemCost).sum();
        double totalWagesCost = statisticsList.stream().mapToDouble(Statistics::getTotalWagesCost).sum();
        int totalNrOfBills = statisticsList.stream().mapToInt(Statistics::getNrOfBills).sum();

        //Add total row
        statisticsList.add(new Statistics(null, totalItemCost, totalWagesCost, totalIncome, totalRevenue, totalNrOfBills));

        ObservableList<Statistics> data = FXCollections.observableArrayList(statisticsList);
        view.getStatisticsTableView().setItems(data);
    }
}
