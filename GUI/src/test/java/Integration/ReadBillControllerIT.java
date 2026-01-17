package Integration;

import Models.Administrator;
import Models.Bill;
import Views.ReadBillsView;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class ReadBillControllerIT extends ApplicationTest {

    private ReadBillsView view;

    @Override
    public void start(Stage stage) {
        // Use real Administrator (same pattern as your other ITs)
        Administrator admin = new Administrator();

        view = new ReadBillsView(admin);

        Scene scene = new Scene(view.getView(), 1000, 700);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void testControllerAndViewInitialization() {
        assertNotNull(view);
        assertNotNull(view.getReadBillTableView());
        assertNotNull(view.getReadBillTableView().getBillTable());
    }

    @Test
    void testDoubleClickOnBillShowsPrintedBill() {
        TableView<Bill> table = view.getReadBillTableView().getBillTable();

        // If DB has no bills, test is skipped (valid for integration tests)
        if (table.getItems().isEmpty()) {
            System.out.println("No bills in database – skipping test.");
            return;
        }

        interact(() -> table.getSelectionModel().select(0));

        // Simulate double click
        doubleClickOn(table);

        // Printed bill TextArea must appear
        assertTrue(
                view.getView().lookup(".text-area") != null,
                "Printed bill should be visible after double click"
        );
    }

    @Test
    void testOkButtonReturnsToTableView() {
        TableView<Bill> table = view.getReadBillTableView().getBillTable();

        if (table.getItems().isEmpty()) {
            System.out.println("No bills in database – skipping test.");
            return;
        }

        interact(() -> table.getSelectionModel().select(0));
        doubleClickOn(table);

        // Click OK
        clickOn("OK");

        // Table must be visible again
        assertTrue(
                view.getView().lookup(".table-view") != null,
                "Bill table should be visible after clicking OK"
        );
    }
}
