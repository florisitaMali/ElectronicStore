package Views;

import Controller.EmployeeMainPageController;
import Models.Employee;
import Models.Permission;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class EmployeesMainPage extends View {
    private final EmployeeMainPageController controller;
    private final BorderPane primaryPane = new BorderPane();
    private final Button generateBill = new Button();
    private final Button readBill = new Button();
    private final Button manageInventory = new Button();
    private final Button manageSectors = new Button();
    private final Button monitorCashierPerformance = new Button();
    private final Button seeStatistics = new Button();
    private final Button manageEmployees = new Button();
    private final Button generateTotalCostAndIncome = new Button();
    private final ImageView backgroundImage = new ImageView(new Image(getClass().getResource("/images/electronicStoreBackgroundPhoto.jpg").toExternalForm()));
    private final MenuBar menuBar = new MenuBar();

    private final MenuItem menuItemGenerateBill = new MenuItem("Generate Bill");
    private final MenuItem menuItemReadBill = new MenuItem("Read Bill");
    private final MenuItem menuItemManageSupplier = new MenuItem("Manage Suppliers");
    private final MenuItem menuItemManageInventory = new MenuItem("Manage Inventory");
    private final MenuItem menuItemManageSectors = new MenuItem("Manage Sectors");
    private final MenuItem menuItemManageEmployees = new MenuItem("Manage Employees");
    private final MenuItem menuItemGenerateTotalCostAndIncome = new MenuItem("Total Cost & Income");
    private final MenuItem menuItemMonitorCashierPerformance = new MenuItem("Cashier Performance");
    private final MenuItem menuItemSeeStatistics = new MenuItem("Statistics (Items sold and Purchased)");
    private final MenuItem menuItemLogOut = new MenuItem("LogOut");
    private final MenuItem menuItemProfile = new MenuItem("Profile");

    // Getters
    public MenuItem getMenuItemGenerateBill() { return menuItemGenerateBill; }
    public MenuItem getMenuItemReadBill() { return menuItemReadBill; }
    public MenuItem getMenuItemManageInventory() { return menuItemManageInventory; }
    public MenuItem getMenuItemManageSectors() { return menuItemManageSectors; }
    public MenuItem getMenuItemManageEmployees() { return menuItemManageEmployees; }
    public MenuItem getMenuItemGenerateTotalCostAndIncome() { return menuItemGenerateTotalCostAndIncome; }
    public MenuItem getMenuItemMonitorCashierPerformance() { return menuItemMonitorCashierPerformance; }
    public MenuItem getMenuItemSeeStatistics() { return menuItemSeeStatistics; }
    public MenuItem getMenuItemLogOut() { return menuItemLogOut; }
    public MenuItem getMenuItemProfile() { return menuItemProfile;}
    public MenuBar getMenuBar() { return menuBar; }
    public Button getGenerateBill() { return generateBill; }
    public Button getReadBill() { return readBill; }
    public Button getManageInventory() { return manageInventory; }
    public Button getManageSectors() { return manageSectors; }
    public Button getMonitorCashierPerformance() { return monitorCashierPerformance; }
    public Button getSeeStatistics() { return seeStatistics; }
    public Button getManageEmployees() { return manageEmployees; }
    public Button getGenerateTotalCostAndIncome() { return generateTotalCostAndIncome; }
    public BorderPane getPrimaryPane() { return primaryPane; }

    public EmployeesMainPage(Employee emp) {
        setCurrentUser(emp);
        controller = new EmployeeMainPageController(this);
        styleButtons();
        setView(emp);
    }

    private void styleButtons() {
        String buttonStyle = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 10px";
        setUpButtonStyle(generateBill, "Generate Bill", "/images/bill.png", buttonStyle);
        setUpButtonStyle(readBill, "Read Bill", "/images/bills.png", buttonStyle);
        setUpButtonStyle(manageInventory, "Manage Inventory", "/images/items.png", buttonStyle);
        setUpButtonStyle(manageSectors, "Manage Sectors", "/images/sector.jpeg", buttonStyle);
        setUpButtonStyle(manageEmployees, "Manage Employees", "/images/employee.png", buttonStyle);
        setUpButtonStyle(generateTotalCostAndIncome, "Total Cost&Income", "/images/costincome.jpeg", buttonStyle);
        setUpButtonStyle(monitorCashierPerformance, "Employee Performance", "/images/employeePerformance.jpg", buttonStyle);
        setUpButtonStyle(seeStatistics, "Statistics", "/images/statistic.jpg", buttonStyle);
    }

    private void setUpButtonStyle(Button button, String text, String imagePath, String style) {
        button.setStyle(style);
        button.setPrefSize(200, 150);

        Text buttonText = new Text(text);
        buttonText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 18));

        ImageView buttonImage = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        buttonImage.setFitHeight(80);
        buttonImage.setFitWidth(80);

        VBox vbox = new VBox(5, buttonImage, buttonText);
        vbox.setAlignment(Pos.CENTER);

        button.setGraphic(vbox);
    }

    private void setView(Employee emp) {
        primaryPane.getChildren().add(backgroundImage);
        backgroundImage.fitHeightProperty().bind(primaryPane.heightProperty());
        backgroundImage.fitWidthProperty().bind(primaryPane.widthProperty());

        // Create Bill menu
        Menu billMenu = new Menu("Bill");
        if (emp.getAccessLevel().contains(Permission.GENERATE_PRINTABLE_BILL)) {
            billMenu.getItems().add(menuItemGenerateBill);
        }
        if (emp.getAccessLevel().contains(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY)) {
            billMenu.getItems().add(menuItemReadBill);
        }

        // Create Manage menu
        Menu manageMenu = new Menu("Manage");
        if (emp.getAccessLevel().contains(Permission.ADD_ITEMS_TO_STOCK)) {
            manageMenu.getItems().add(menuItemManageInventory);
        }
        if (emp.getAccessLevel().contains(Permission.SUPPLY_SECTOR_WITH_NEEDED_ITEMS)) {
            manageMenu.getItems().add(menuItemManageSectors);
        }
        if (emp.getAccessLevel().contains(Permission.MANAGE_EMPLOYEES)) {
            manageMenu.getItems().add(menuItemManageEmployees);
        }

        // Create Statistic menu
        Menu statisticMenu = new Menu("Statistic");
        if (emp.getAccessLevel().contains(Permission.GENERATE_TOTAL_COST_INCOME)) {
            statisticMenu.getItems().add(menuItemGenerateTotalCostAndIncome);
        }
        if (emp.getAccessLevel().contains(Permission.MONITOR_CASHIER_PERFORMANCE)) {
            statisticMenu.getItems().add(menuItemMonitorCashierPerformance);
        }
        if (emp.getAccessLevel().contains(Permission.ACCESS_STATISTICS_ABOUT_SOLD_AND_PURCHASED_ITEMS)) {
            statisticMenu.getItems().add(menuItemSeeStatistics);
        }

        // Add profile and log out menu items
        Menu profileMenu = new Menu("Profile");
        profileMenu.getItems().add(menuItemProfile);
        profileMenu.getItems().add(menuItemLogOut);

        // Add menus to menu bar
        menuBar.getMenus().addAll(billMenu, manageMenu, statisticMenu, profileMenu);

        primaryPane.setTop(menuBar);

        // Setup the center buttons
        HBox vBox = new HBox(5);
        if (emp.getAccessLevel().contains(Permission.GENERATE_PRINTABLE_BILL)) {
            vBox.getChildren().add(generateBill);
        }
        if (emp.getAccessLevel().contains(Permission.VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY)) {
            vBox.getChildren().add(readBill);
        }
        if (emp.getAccessLevel().contains(Permission.ADD_ITEMS_TO_STOCK)) {
            vBox.getChildren().add(manageInventory);
        }
        if (emp.getAccessLevel().contains(Permission.SUPPLY_SECTOR_WITH_NEEDED_ITEMS)) {
            vBox.getChildren().add(manageSectors);
        }
        if (emp.getAccessLevel().contains(Permission.MANAGE_EMPLOYEES)) {
            vBox.getChildren().add(manageEmployees);
        }
        if (emp.getAccessLevel().contains(Permission.GENERATE_TOTAL_COST_INCOME)) {
            vBox.getChildren().add(generateTotalCostAndIncome);
        }
        if (emp.getAccessLevel().contains(Permission.MONITOR_CASHIER_PERFORMANCE)) {
            vBox.getChildren().add(monitorCashierPerformance);
        }
        if (emp.getAccessLevel().contains(Permission.ACCESS_STATISTICS_ABOUT_SOLD_AND_PURCHASED_ITEMS)) {
            vBox.getChildren().add(seeStatistics);
        }

        vBox.setAlignment(Pos.CENTER);
        primaryPane.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.CENTER);
    }

    @Override
    public Parent getView() {
        return primaryPane;
    }
}
