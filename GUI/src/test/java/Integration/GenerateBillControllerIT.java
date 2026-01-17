package Integration;

import Controller.GenerateBillController;
import DAO.BillDAO;
import DAO.DBConnection;
import Models.*;
import Views.GenerateBillView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenerateBillControllerIT {

    private GenerateBillView view;
    private GenerateBillController controller;
    private Employee employee;
    private FxRobot robot;
    private int categoryId;
    private int supplierId;

    @Start
    void start(Stage stage) {
        employee = new Administrator();
        view = new GenerateBillView(employee);
        controller = new GenerateBillController(view);

        stage.setScene(new Scene(view.getView(), 900, 600));
        stage.show();
    }

    @BeforeAll
    void setupDatabaseData() {
        deleteAllBills();
        deleteAllItems();
        deleteAllCategories();
        deleteAllSuppliers();

        categoryId = insertCategory("TEST_CATEGORY2", 1);
        supplierId = insertSupplier("TEST_SUPPLIER2");

        insertItem("TEST_ITEM", 100, 10.0, 20.0, categoryId, supplierId, 5);
    }

    @BeforeEach
    void setupRobot() {
        robot = new FxRobot();
    }

    @BeforeEach
    void resetBill(FxRobot robot) {
        robot.interact(() -> view.createNewBill());
    }

    @Test
    void addSoldItem_updatesBillModel(FxRobot robot) {
        robot.interact(() -> {
            view.getItemNameTF().setText("TEST_ITEM");
            view.getItemQuantityTF().setText("2");
        });

        robot.clickOn(view.getAddSoldItemBtn());

        Bill bill = view.getBill();
        assertEquals(1, bill.getSoldItems().size());
        assertEquals("TEST_ITEM", bill.getSoldItems().get(0).getItemName());
        assertEquals(2, bill.getSoldItems().get(0).getSoldQuantity());
    }

    @ParameterizedTest
    @CsvSource({
            "TEST_ITEM, 500",
            "TEST_ITEM, -5",
            "TEST_ITEM, 0"
    })
    void addInvalidQuantity_doesNotModifyBill(String itemName, int quantity) {
        robot.interact(() -> {
            view.getItemNameTF().setText(itemName);
            view.getItemQuantityTF().setText(String.valueOf(quantity));
        });

        robot.clickOn(view.getAddSoldItemBtn());

        assertTrue(view.getBill().getSoldItems().isEmpty());
    }

    @Test
    void deleteSoldItem_updatesBillModel(FxRobot robot) {
        robot.interact(() -> {
            view.getItemNameTF().setText("TEST_ITEM");
            view.getItemQuantityTF().setText("2");
        });

        robot.clickOn(view.getAddSoldItemBtn());
        robot.clickOn(view.getDeleteSoldItemBtn());

        robot.interact(() -> {
            view.getDeleteItemView()
                    .getSoldItemCheckBox()
                    .get(0)
                    .setSelected(true);
        });

        robot.clickOn(view.getDeleteItemView().getApproveBtn());

        assertTrue(view.getBill().getSoldItems().isEmpty());
    }

    @Test
    void printBill_savesBillToDatabase(FxRobot robot) {
        robot.interact(() -> {
            view.getItemNameTF().setText("TEST_ITEM");
            view.getItemQuantityTF().setText("3");
        });

        robot.clickOn(view.getAddSoldItemBtn());
        robot.clickOn(view.getPrintBillBtn());

        assertFalse(
                BillDAO.getDayBills(employee).isEmpty(),
                "Printed bill must be saved in DB"
        );
    }

    private int insertCategory(String name, int sectorId) {
        return executeInsert(
                "INSERT INTO categories(name, sector) VALUES (?,?)",
                name, sectorId
        );
    }

    private int insertSupplier(String name) {
        return executeInsert(
                "INSERT INTO suppliers(name, address) VALUES (?, 'Test Address')",
                name
        );
    }

    private void insertItem(String name, int qty, double pp, double sp,
                            int categoryId, int supplierId, int stockLimit) {

        String sql = """
            INSERT INTO items
            (name, quantity, purchased_price, selling_price,
             purchased_date, category_id, supplier_id, stock_limit, deleted)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, qty);
            ps.setDouble(3, pp);
            ps.setDouble(4, sp);
            ps.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt(6, categoryId);
            ps.setInt(7, supplierId);
            ps.setLong(8, stockLimit);
            ps.executeUpdate();

        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    private int executeInsert(String sql, Object... params) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            fail(e.getMessage());
            return -1;
        }
    }

    // ================= CLEANUP =================

    private static void deleteAllBills() {
        executeDelete("DELETE FROM bill_items");
        executeDelete("DELETE FROM bills");
    }

    private static void deleteAllItems() {
        executeDelete("DELETE FROM items");
    }

    private static void deleteAllCategories() {
        executeDelete("DELETE FROM categories");
    }

    private static void deleteAllSuppliers() {
        executeDelete("DELETE FROM suppliers");
    }

    private static void executeDelete(String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }
}
