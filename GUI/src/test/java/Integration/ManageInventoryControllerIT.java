package Integration;

import Controller.ManageInventoryController;
import DAO.DBConnection;
import DAO.ItemsDAO;
import DAO.SuppliersDAO;
import Models.*;
import Views.ManageInventoryView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManageInventoryControllerIT {

    private ManageInventoryView view;
    private ManageInventoryController controller;
    private Employee admin;
    private FxRobot robot;

    @Start
    void start(Stage stage) {
        admin = new Administrator(); // use admin for testing
        view = new ManageInventoryView(admin);
        controller = new ManageInventoryController(view);
        robot = new FxRobot();
        stage.setScene(new Scene(view.getView(), 800, 600));
        stage.show();
    }

    @BeforeEach
    void setupDatabase() {
        clearAllItems();
        clearAllSuppliers();
        view.getItemList().clear();
        view.getInventoryTable().getItems().clear();

        // Add temporary test category and supplier
        addTestCategory("Test_Category", Sector.CAMERA);
        addTestSupplier("Test_Supplier");
    }

    @AfterEach
    void cleanupDatabase() {
        // Remove the temporary test category and supplier
        deleteTestCategory("Test_Category");
        deleteTestSupplier("Test_Supplier");
    }

    // =================== ADD ITEM ===================
    @ParameterizedTest
    @CsvSource({
            "'', 10, CAMERA_LAPTOPS, Test_Supplier, 10, 20, 5, 'itemName'",       // empty name
            "Test Item 1, -1, CAMERA_LAPTOPS, Test_Supplier, 10, 20, 5, 'quantity'", // negative quantity
            "Test Item 2, 10, CAMERA_LAPTOPS, Test_Supplier, -10, 20, 5, 'purchasedPrice'", // negative purchasedPrice
            "Test Item 3, 10, CAMERA_LAPTOPS, Test_Supplier, 10, -20, 5, 'sellingPrice'", // negative sellingPrice
            "Test Item 4, 10, CAMERA_LAPTOPS, Test_Supplier, 10, 20, -5, 'stockLimit'"    // negative stockLimit
    })
    void addItem_withEmptyName_shouldNotAdd(String newName, int newQuantity,  String category, String supplierName, double purchasedPrice, double sellingPrice, long stockLimit, String invalidField) {
        int before = view.getItemList().size();
        robot.clickOn(view.getAddButton());
        fillItemDialog(robot, newName, newQuantity, category, supplierName, purchasedPrice, sellingPrice, stockLimit);
        robot.clickOn("Save");
        int after = view.getItemList().size();
        assertEquals(before, after, "Item with empty name should not be added");
    }

    @Test
    void addItem_withNegativeQuantity_shouldNotAdd(FxRobot robot) {
        int before = view.getItemList().size();
        robot.clickOn(view.getAddButton());
        fillItemDialog(robot, "Laptop", -5, "CAMERA_LAPTOPS", "Test_Supplier", 500, 700, 10);
        robot.clickOn("Save");
        int after = view.getItemList().size();
        assertEquals(before, after, "Item with negative quantity should not be added");
    }

    @Test
    void addItem_withValidData_shouldAdd(FxRobot robot) {
        int before = view.getItemList().size();
        robot.clickOn(view.getAddButton());
        fillItemDialog(robot, "Laptop", 5, "CAMERA_LAPTOPS", "Test_Supplier", 500, 700, 10);
        robot.clickOn("Save");
        int after = view.getItemList().size();
        assertEquals(before + 1, after, "Valid item should be added");
    }

    // =================== EDIT ITEM ===================
    @Test
    void editItem_withoutSelection_shouldShowAlert(FxRobot robot) {
        robot.interact(() -> view.getEditButton().fire());
        assertTrue(view.getItemList().isEmpty(), "Edit with no selection should not modify list");
    }

    @ParameterizedTest
    @CsvSource({
            "'', 10, CAMERA_LAPTOPS, Test_Supplier, 10, 20, 5, 'itemName'",       // empty name
            "Test Item, -1, CAMERA_LAPTOPS, Test_Supplier, 10, 20, 5, 'quantity'", // negative quantity
            "Test Item, 10, CAMERA_LAPTOPS, Test_Supplier, -10, 20, 5, 'purchasedPrice'", // negative purchasedPrice
            "Test Item, 10, CAMERA_LAPTOPS, Test_Supplier, 10, -20, 5, 'sellingPrice'", // negative sellingPrice
            "Test Item, 10, CAMERA_LAPTOPS, Test_Supplier, 10, 20, -5, 'stockLimit'"    // negative stockLimit
    })
    void editItem_invalidField_shouldPreserveOldValue(String newName, int newQuantity,  String category, String supplierName, double purchasedPrice, double sellingPrice, long stockLimit, String invalidField) {
        Item original = new Item("Test", 10, new Category("Test_Category", Sector.CAMERA),
                new Supplier("Test_Supplier"), 10, 20, 5);
        robot.interact(() -> {
            view.getItemList().add(original);
            view.getInventoryTable().getItems().add(original);
            view.getInventoryTable().getSelectionModel().select(original);
        });

        // Open edit dialog
        robot.clickOn(view.getEditButton());

        // Fill the dialog with the parameters
        fillItemDialog(robot, newName, newQuantity, category, supplierName, purchasedPrice, sellingPrice, stockLimit);

        // Click save
        robot.clickOn("Save");

        // Retrieve the edited item
        Item edited = view.getItemList().get(0);

        // Assertions: check the invalid field did not change, others remain valid
        switch (invalidField) {
            case "itemName" -> assertEquals("Test", edited.getItemName(), "Item name should not change to invalid value");
            case "quantity" -> assertEquals(10, edited.getQuantity(), "Quantity should not change to invalid value");
            case "purchasedPrice" -> assertEquals(10, edited.getPurchasedPrice(), "Purchased price should not change to invalid value");
            case "sellingPrice" -> assertEquals(20, edited.getSellingPrice(), "Selling price should not change to invalid value");
            case "stockLimit" -> assertEquals(5, edited.getStockLimit(), "Stock limit should not change to invalid value");
        }
    }


    // =================== DELETE ITEM ===================
    @Test
    void deleteItem_withoutSelection_shouldShowAlert(FxRobot robot) {
        robot.interact(() -> view.getDeleteButton().fire());
        assertTrue(view.getItemList().isEmpty(), "Delete with no selection should not modify list");
    }

    @Test
    void deleteItem_withSelection_removesItem(FxRobot robot) {
        Item item = new Item("Test", 10, new Category("Test_Category", Sector.CAMERA), new Supplier("Test_Supplier"), 10, 20, 5);
        robot.interact(() -> {
            view.getItemList().add(item);
            view.getInventoryTable().getItems().add(item);
            view.getInventoryTable().getSelectionModel().select(item);
        });
        robot.interact(() -> view.getDeleteButton().fire());
        assertTrue(view.getItemList().isEmpty(), "Selected item should be deleted");
    }

    // =================== SEARCH ITEM ===================
    @Test
    void searchItem_nonExistent_shouldReturnEmptyList(FxRobot robot) {
        Item item = new Item("Item1", 10, new Category("Test_Category", Sector.CAMERA), new Supplier("Test_Supplier"), 10, 20, 5);
        robot.interact(() -> {
            view.getItemList().add(item);
            view.getInventoryTable().getItems().add(item);
        });

        robot.clickOn(view.getSearchField()).write("NonExist");
        robot.clickOn(view.getSearchButton());

        assertTrue(view.getInventoryTable().getItems().isEmpty(), "Searching non-existent item should show empty table");
    }

    // =================== HELPERS ===================
    private void fillItemDialog(FxRobot robot, String name, int quantity, String categoryName,
                                String supplierName, double purchasedPrice, double sellingPrice, long stockLimit) {
        // Step 2: Wait for dialog to appear and get dialog pane
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);

        // Step 3: Fill in the fields using IDs
        robot.clickOn("#itemNameField").eraseText(20).write(name);
        robot.clickOn("#quantityField").eraseText(10).write(String.valueOf(quantity));

        // Select the first RadioButton inside the category VBox
        RadioButton firstCategory = robot.from(dialogPane)
                .lookup(".radio-button")   // all radio buttons inside dialog
                .queryAllAs(RadioButton.class)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No category radio button found"));

        robot.interact(firstCategory::fire);
        // Step 5: Select supplier
        ComboBox<Supplier> supplierComboBox = robot.from(dialogPane)
                .lookup("#supplierComboBox")
                .queryAs(ComboBox.class);
        robot.interact(() -> supplierComboBox.getSelectionModel().select(0));

        // Step 6: Fill other numeric fields
        robot.clickOn("#purchasedPriceField").eraseText(10).write(String.valueOf(purchasedPrice));
        robot.clickOn("#sellingPriceField").eraseText(10).write(String.valueOf(sellingPrice));
        robot.clickOn("#stockLimitField").eraseText(10).write(String.valueOf(stockLimit));
    }

    public void clearAllItems() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM items")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearAllSuppliers() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM suppliers")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addTestCategory(String categoryName, Sector sector) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO categories (name, sector) VALUES (?, ?)")) {
            ps.setString(1, categoryName);
            ps.setInt(2, 2);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTestSupplier(String supplierName) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO suppliers (name, address) VALUES (?, 'Test Address')")) {
            ps.setString(1, supplierName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTestCategory(String categoryName) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM categories WHERE name = ?")) {
            ps.setString(1, categoryName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTestSupplier(String supplierName) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM suppliers WHERE name = ?")) {
            ps.setString(1, supplierName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
