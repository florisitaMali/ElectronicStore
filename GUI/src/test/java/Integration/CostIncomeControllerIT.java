package Integration;

import Controller.CostIncomeController;
import DAO.*;
import Models.*;
import Views.TotalCostIncomeView;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class CostIncomeControllerIT {

    private TotalCostIncomeView view;
    private CostIncomeController controller;

    @BeforeEach
    void setup() {
        deleteAllBillsForUser(view.getCurrentUser().getId());
    }

    @Start
    void start(Stage stage) {
        Administrator adm = EmployeeDAO.getAdministrator();
        view = new TotalCostIncomeView(adm);
        controller = new CostIncomeController(view);
        stage.setScene(new Scene(view.getView(), 1400, 800));
        stage.show();
    }


    @Test
    void calculateStatisticsWithValidDatesShowsResults(FxRobot robot) {
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now();

        robot.interact(() -> {
            view.getStartDatePicker().setValue(start);
            view.getEndDatePicker().setValue(end);
        });

        robot.clickOn(view.getCalculateButton());

        TableView<Statistics> table = view.getStatisticsTableView();
        assertFalse(table.getItems().isEmpty(), "Table should have statistics");

        // Last row is total
        Statistics total = table.getItems().get(table.getItems().size() - 1);
        assertTrue(total.getTotalIncome() >= 0, "Total income should be >= 0");
    }

    // --- Invalid case: startDate null ---
    @Test
    void calculateStatisticsWithNullStartDateDoesNothing(FxRobot robot) {
        LocalDate end = LocalDate.now();

        robot.interact(() -> {
            view.getStartDatePicker().setValue(null);
            view.getEndDatePicker().setValue(end);
        });

        robot.clickOn(view.getCalculateButton());

        TableView<Statistics> table = view.getStatisticsTableView();
        assertTrue(table.getItems().isEmpty(), "Table should remain empty for null start date");
    }

    // --- Invalid case: endDate null ---
    @Test
    void calculateStatisticsWithNullEndDateDoesNothing(FxRobot robot) {
        LocalDate start = LocalDate.now().minusDays(5);

        robot.interact(() -> {
            view.getStartDatePicker().setValue(start);
            view.getEndDatePicker().setValue(null);
        });

        robot.clickOn(view.getCalculateButton());

        TableView<Statistics> table = view.getStatisticsTableView();
        assertTrue(table.getItems().isEmpty(), "Table should remain empty for null end date");
    }

    // --- Invalid case: startDate after endDate ---
    @Test
    void calculateStatisticsWithStartDateAfterEndDateDoesNothing(FxRobot robot) {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().minusDays(5);

        robot.interact(() -> {
            view.getStartDatePicker().setValue(start);
            view.getEndDatePicker().setValue(end);
        });

        robot.clickOn(view.getCalculateButton());

        TableView<Statistics> table = view.getStatisticsTableView();
        assertTrue(table.getItems().isEmpty(), "Table should remain empty if start > end");
    }

    @Test
    void calculateStatisticsWithBillsAggregatesCorrectly(FxRobot robot) {
        // Create test items
        try {
            CategoryDAO.addCategory(new Category("TEST_CATEGORY", Sector.COMPUTERS));
            SuppliersDAO.addSupplier(new Supplier("Test Supplier"));
        }catch (Exception e){}
        Item item1 = ItemsDAO.searchItem("TEST_ITEM_1");
        if (item1 == null) {
            item1 = new Item("TEST_ITEM_1", 20, new Category("TEST_CATEGORY", Sector.COMPUTERS), new Supplier("Test Supplier"), 100.0, 150.0, 2);
            ItemsDAO.addItem(item1);
        }

        Item item2 = ItemsDAO.searchItem("TEST_ITEM_2");
        if (item2 == null) {
            item2 = new Item("TEST_ITEM_2", 20, new Category("TEST_CATEGORY", Sector.COMPUTERS), new Supplier("Test Supplier"), 90.0, 200.0, 2);
            ItemsDAO.addItem(item2);
        }
        double totalIncome = 0.0;
        // Create bills
        Bill b1 = new Bill(view.getCurrentUser());
        b1.addSoldItems(new SoldItem("TEST_ITEM_1", 2));
        totalIncome = item1.getSellingPrice() * 2;
        BillDAO.saveBill(b1);

        Bill b2 = new Bill(view.getCurrentUser());
        b2.addSoldItems(new SoldItem("TEST_ITEM_2", 2));
        totalIncome += item2.getSellingPrice() * 2;
        BillDAO.saveBill(b2);

        LocalDate today = LocalDate.now();
        robot.interact(() -> {
            view.getStartDatePicker().setValue(today);
            view.getEndDatePicker().setValue(today);
        });

        robot.clickOn(view.getCalculateButton());

        TableView<Statistics> table = view.getStatisticsTableView();
        // Include daily + total row
        assertEquals(2, table.getItems().size());

        Statistics daily = table.getItems().get(0);
        assertEquals(BillDAO.getDayBills(view.getCurrentUser()).size(), daily.getNrOfBills());
        double expectedIncome = b1.getTotalPrice() + b2.getTotalPrice();
        assertEquals(totalIncome, expectedIncome);
    }

    //create the method here to delete all bills for a user
    public static void deleteAllBillsForUser(int employeeId) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // First delete bill items (foreign key constraint)
            String deleteItemsSql = """
            DELETE bi FROM bill_items bi
            JOIN bills b ON bi.bill_id = b.id
            WHERE b.employee_id = ?
        """;
            try (PreparedStatement ps = con.prepareStatement(deleteItemsSql)) {
                ps.setInt(1, employeeId);
                ps.executeUpdate();
            }

            // Then delete bills
            String deleteBillsSql = "DELETE FROM bills WHERE employee_id = ?";
            try (PreparedStatement ps = con.prepareStatement(deleteBillsSql)) {
                ps.setInt(1, employeeId);
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

}
