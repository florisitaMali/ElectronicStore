package Controller;

import Models.Employee;
import Models.Permission;
import Views.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmployeeMainPageController {
    private final EmployeesMainPage view;

    public EmployeeMainPageController(EmployeesMainPage view) {
        this.view = view;
        enableButtons();
    }

    private void enableButtons() {
        setGenerateBillAction();
        setReadBillButtonAction();
        setManageInventoryButtonAction();
        setManageSectorsButtonAction();
        setMonitorCashierPerformanceButtonAction();
        setSeeStatisticsButtonAction();
        setManageEmployeesButtonAction();
        setGenerateTotalCostAndIncomeButtonAction();
        initializeMenuActions();
    }

    private void setGenerateBillAction() {
        view.getGenerateBill().setOnAction(e -> {
            GenerateBillView main = new GenerateBillView(view.getCurrentUser());
            showPage(main);
        });
    }

    private void setReadBillButtonAction() {
        view.getReadBill().setOnAction(e -> {
            ReadBillsView main = new ReadBillsView(view.getCurrentUser());
            showPage(main);
        });
    }

    private void setManageInventoryButtonAction() {
        view.getManageInventory().setOnAction(e -> {
            ManageInventoryView main = new ManageInventoryView(view.getCurrentUser());
            showPage(main);
        });
    }

    private void setManageSectorsButtonAction() {
        view.getManageSectors().setOnAction(e -> {
            ManagerSectorsView main = new ManagerSectorsView(view.getCurrentUser());
            showPage(main);
        });
    }

    private void setMonitorCashierPerformanceButtonAction() {
        view.getMonitorCashierPerformance().setOnAction(e -> {
            MonitorEmployeesPerformanceView main = new MonitorEmployeesPerformanceView(view.getCurrentUser());
            showPage(main);
        });
    }

    private void setSeeStatisticsButtonAction() {
        view.getSeeStatistics().setOnAction(e -> {
            MonitorStatisticsView main = new MonitorStatisticsView(view.getCurrentUser());
            showPage(main);
        });
    }

    private void setManageEmployeesButtonAction() {
        view.getManageEmployees().setOnAction(e -> {
            AdminManageEmployeesView main = new AdminManageEmployeesView(view.getCurrentUser());
            showPage(main);
        });
    }

    private void setGenerateTotalCostAndIncomeButtonAction() {
        view.getGenerateTotalCostAndIncome().setOnAction(e -> {
            TotalCostIncomeView main = new TotalCostIncomeView(view.getCurrentUser());
            showPage(main);
        });
    }

    private void initializeMenuActions() {
        Employee emp = view.getCurrentUser();
        for(Permission p: emp.getAccessLevel())
        {
            System.out.println(p);
        }
        System.out.println("acccesss");

        if (emp.getAccessLevel().contains(Permission.GENERATE_PRINTABLE_BILL)) {
            view.getMenuItemGenerateBill().setOnAction(e -> {
                GenerateBillView main = new GenerateBillView(view.getCurrentUser());
                showPage(main);
            });
        }
        if (emp.getAccessLevel().contains(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY)) {
            view.getMenuItemReadBill().setOnAction(e -> {
                ReadBillsView main = new ReadBillsView(view.getCurrentUser());
                showPage(main);
            });
        }
        if (emp.getAccessLevel().contains(Permission.ADD_ITEMS_TO_STOCK)) {
            view.getMenuItemManageInventory().setOnAction(e -> {
                ManageInventoryView main = new ManageInventoryView(view.getCurrentUser());
                showPage(main);
            });
        }
        if (emp.getAccessLevel().contains(Permission.SUPPLY_SECTOR_WITH_NEEDED_ITEMS)) {
            view.getMenuItemManageSectors().setOnAction(e -> {
                ManagerSectorsView main = new ManagerSectorsView(view.getCurrentUser());
                showPage(main);
            });
        }
        if (emp.getAccessLevel().contains(Permission.MANAGE_EMPLOYEES)) {
            view.getMenuItemManageEmployees().setOnAction(e -> {
                AdminManageEmployeesView main = new AdminManageEmployeesView(view.getCurrentUser());
                showPage(main);
            });
        }
        if (emp.getAccessLevel().contains(Permission.GENERATE_TOTAL_COST_INCOME)) {
            view.getMenuItemGenerateTotalCostAndIncome().setOnAction(e -> {
                TotalCostIncomeView main = new TotalCostIncomeView(view.getCurrentUser());
                showPage(main);
            });
        }
        if (emp.getAccessLevel().contains(Permission.MONITOR_CASHIER_PERFORMANCE)) {
            view.getMenuItemMonitorCashierPerformance().setOnAction(e -> {
                MonitorEmployeesPerformanceView main = new MonitorEmployeesPerformanceView(view.getCurrentUser());
                showPage(main);
            });
        }
        if (emp.getAccessLevel().contains(Permission.ACCESS_STATISTICS_ABOUT_SOLD_AND_PURCHASED_ITEMS)) {
            view.getMenuItemSeeStatistics().setOnAction(e -> {
                MonitorStatisticsView main = new MonitorStatisticsView(view.getCurrentUser());
                showPage(main);
            });
        }

        view.getMenuItemProfile().setOnAction(e->
        {
            Profile main = new Profile(view.getCurrentUser());
            showPage(main);
        });

        view.getMenuItemLogOut().setOnAction(e -> {
            MainPageView main = new MainPageView();
            Stage stage = (Stage) view.getPrimaryPane().getScene().getWindow();
            new MainPageController(main, stage);
            Scene scene = new Scene(main.getView(), 800, 500);
            stage.setScene(scene);
            stage.show();
        });
    }

    private void showPage(View main) {
        view.getPrimaryPane().setCenter(main.getView());
    }
}
