package Integration;

import DAO.*;
import Models.*;
import Views.ReadBillsView;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReadBillControllerIT extends ApplicationTest {

    private ReadBillsView view;
    private BillDAO billDAO;
    private List<Bill> insertedBills;

    @Override
    public void start(Stage stage) {
        Administrator admin = EmployeeDAO.getAdministrator();
        view = new ReadBillsView(admin);

        Scene scene = new Scene(view.getView(), 1000, 700);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    void insertBillsBeforeEachTest() {
        Administrator admin = EmployeeDAO.getAdministrator();
        insertedBills = new ArrayList<>();

        Category electronics = new Category("Electronics" + "", Sector.ELECTRONICS);
        Supplier supplier = new Supplier("Supplier_A");

        Item laptop = null;
        Item mouse = null;

        try {
            laptop = new Item("Laptop", 10, electronics, supplier, 700.0, 950.0, 2);
            mouse = new Item("Mouse", 50, electronics, supplier, 10.0, 25.0, 5);
        } catch (ItemNotAvailableException e) {
            fail("Item creation failed during test setup: " + e.getMessage());
        }

        CategoryDAO.addCategory(electronics);
        SuppliersDAO.addSupplier(supplier);
        ItemsDAO.addItem(laptop);
        ItemsDAO.addItem(mouse);

        Bill bill1 = new Bill(admin);
        Bill bill2 = new Bill(admin);

        bill1.addSoldItems(new SoldItem("Laptop", 1));
        bill1.addSoldItems(new SoldItem("Mouse", 2));

        bill2.addSoldItems(new SoldItem("Mouse", 5));

        BillDAO.saveBill(bill1);
        BillDAO.saveBill(bill2);

        System.out.println(BillDAO.getAllBills().size());

        insertedBills.add(bill1);
        insertedBills.add(bill2);
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

        assertFalse(table.getItems().isEmpty());

        interact(() -> table.getSelectionModel().select(0));
        doubleClickOn(table);

        assertNotNull(view.getView().lookup(".text-area"), "Printed bill should be visible after double click");
    }

    @Test
    void testOkButtonReturnsToTableView() {
        TableView<Bill> table = view.getReadBillTableView().getBillTable();

        assertFalse(table.getItems().isEmpty(), "Bills must exist for test");

        interact(() -> table.getSelectionModel().select(0));
        doubleClickOn(table);

        clickOn("OK");

        assertNotNull(view.getView().lookup(".table-view"), "Bill table should be visible after clicking OK");
    }


    @AfterAll
    void cleanup() {
        deleteBillItems();
        deleteBills();
        deleteItems();
        deleteCategories();
        deleteSuppliers();
    }

    private void deleteBillItems() {
        executeDelete("DELETE FROM bill_items");
    }

    private void deleteBills() {
        executeDelete("DELETE FROM bills");
    }

    private void deleteItems() {
        executeDelete("DELETE FROM items");
    }

    private void deleteCategories() {
        executeDelete("DELETE FROM categories");
    }

    private void deleteSuppliers() {
        executeDelete("DELETE FROM suppliers");
    }

    private static void executeDelete(String sql) {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            int rows = ps.executeUpdate();
            System.out.println("Deleted " + rows + " rows with query: " + sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
