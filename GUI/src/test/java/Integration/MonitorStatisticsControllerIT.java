package Integration;

import Controller.MonitorStatisticsController;
import Models.Administrator;
import Models.Employee;
import Views.MonitorStatisticsView;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class MonitorStatisticsControllerIT {

    private MonitorStatisticsView view;
    private MonitorStatisticsController controller;

    @Start
    void start(Stage stage) {
        Employee admin = new Administrator();
        view = new MonitorStatisticsView(admin);
        controller = new MonitorStatisticsController(view);

        stage.setScene(view.getView().getScene());
        stage.show();
    }

    @Test
    void controllerInitializesCorrectly(FxRobot robot) {
        assertNotNull(controller);
        assertNotNull(view);
    }

    @Test
    void viewComponentsExist(FxRobot robot) {
        assertNotNull(view.getStartDatePicker());
        assertNotNull(view.getEndDatePicker());
        assertNotNull(view.getItemsSoldChart());
        assertNotNull(view.getItemsPurchasedChart());
        assertNotNull(view.getStatsSummary());
    }

    @Test
    void statisticsAreLoadedOnStartup(FxRobot robot) {
        String summary = view.getStatsSummary().getText();
        assertNotNull(summary);
        assertFalse(summary.isEmpty());
    }

    @Test
    void datePickerInteractionTriggersUpdate(FxRobot robot) {
        robot.interact(() -> {
            view.getStartDatePicker().setValue(LocalDate.now().minusDays(2));
            view.getEndDatePicker().setValue(LocalDate.now());
        });

        robot.interact(() -> view.getStartDatePicker().fireEvent(new javafx.event.ActionEvent()));
        robot.interact(() -> view.getEndDatePicker().fireEvent(new javafx.event.ActionEvent()));

        assertNotNull(view.getStatsSummary().getText());
    }

    @Test
    void chartsArePresentAndAccessible(FxRobot robot) {
        PieChart sold = view.getItemsSoldChart();
        PieChart purchased = view.getItemsPurchasedChart();

        assertNotNull(sold.getData());
        assertNotNull(purchased.getData());
    }

    @Test
    void chartTitlesAreSet(FxRobot robot) {
        assertEquals("Items Sold Statistics", view.getItemsSoldChart().getTitle());
        assertEquals("Items Purchased Statistics", view.getItemsPurchasedChart().getTitle());
    }

}
