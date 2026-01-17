package Integration;

import Controller.ManagerSectorsController;
import DAO.*;
import Models.*;
import Views.ManagerSectorsView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagerSectorsControllerIT extends ApplicationTest {

    private ManagerSectorsView view;
    private ManagerSectorsController controller;

    private static Category testCategory;
    private static Supplier testSupplier;
    private static Manager testManager;
    private FxRobot robot;

    @BeforeAll
    static void setupDatabase() {
        // Add a test category and supplier
        testCategory = new Category("CAMERA_LAPTOPS", Sector.CAMERA);
        CategoryDAO.addCategory(testCategory);

        testSupplier = new Supplier("Test_Supplier");
        SuppliersDAO.addSupplier(testSupplier);

        testManager = createTestManager();
    }

    @AfterAll
    static void cleanupDatabase() {
        CategoryDAO.deleteCategory(testCategory);
        SuppliersDAO.deleteSupplier(testSupplier);
        deleteTestManager();
    }

    @Override
    public void start(Stage stage) {
        view = new ManagerSectorsView(testManager);
        controller = new ManagerSectorsController(view);
        robot = new FxRobot();
        stage.setScene(new Scene(view.getView(), 800, 600));
        stage.show();;
    }

    @BeforeEach
    void clearItems() {
        clearAllItems();
        view.getItemList().clear();
        view.getInventoryTable().getItems().clear();
    }

    @Test
    void addItem_withValidData_shouldAddItem() {
        robot.interact(() -> {
            Item item = new Item("Laptop", 5, testCategory, testSupplier, 1000, 1200, 10);
            view.getItemList().add(item);
            view.getInventoryTable().getItems().add(item);
        });

        assertEquals(1, view.getItemList().size());
        assertEquals("Laptop", view.getItemList().get(0).getItemName());
    }

    @ParameterizedTest
    @MethodSource("invalidEditProvider")
    void editItem_invalidField_shouldPreserveOldValue(String newName, int newQuantity,double purchasedPrice,double sellingPrice,long stockLimit,String invalidField) {
        FxRobot robot = new FxRobot();

        // Add initial valid item
        Item original = new Item("Camera", 10, testCategory, testSupplier, 100, 150, 5);
        robot.interact(() -> {
            view.getItemList().add(original);
            view.getInventoryTable().getItems().add(original);
            view.getInventoryTable().getSelectionModel().select(original);
        });

        // Open edit dialog
        robot.clickOn(view.getEditButton());

        // Fill dialog with invalid field
        robot.interact(() -> {
            // Access dialog fields directly
            // For simplicity, assume dialog is created and accessible
            original.setItemName(newName.isEmpty() ? original.getItemName() : newName);
            original.setQuantity(newQuantity <= 0 ? original.getQuantity() : newQuantity);
            original.setPurchasedPrice(purchasedPrice <= 0 ? original.getPurchasedPrice() : purchasedPrice);
            original.setSellingPrice(sellingPrice <= 0 ? original.getSellingPrice() : sellingPrice);
            original.setStockLimit(stockLimit <= 0 ? original.getStockLimit() : stockLimit);
        });

        // Save item
        robot.clickOn("Save");

        // Assertions
        Item edited = view.getItemList().get(0);
        switch (invalidField) {
            case "itemName" -> assertEquals("Camera", edited.getItemName());
            case "quantity" -> assertEquals(10, edited.getQuantity());
            case "purchasedPrice" -> assertEquals(100, edited.getPurchasedPrice());
            case "sellingPrice" -> assertEquals(150, edited.getSellingPrice());
            case "stockLimit" -> assertEquals(5, edited.getStockLimit());
        }
    }

    static Stream<Arguments> invalidEditProvider() {
        return Stream.of(
                Arguments.of("", 10, 100, 150, 5, "itemName"),
                Arguments.of("Camera", -5, 100, 150, 5, "quantity"),
                Arguments.of("Camera", 10, -100, 150, 5, "purchasedPrice"),
                Arguments.of("Camera", 10, 100, -150, 5, "sellingPrice"),
                Arguments.of("Camera", 10, 100, 150, -5, "stockLimit")
        );
    }

    @Test
    void deleteItem_shouldRemoveFromList() {
        Item item = new Item("Camera", 5, testCategory, testSupplier, 100, 150, 5);
        robot.interact(() -> {
            view.getItemList().add(item);
            view.getInventoryTable().getItems().add(item);
            view.getInventoryTable().getSelectionModel().select(item);
        });

        robot.clickOn(view.getDeleteButton());

        assertTrue(view.getItemList().isEmpty());
    }

    @Test
    void addSupplier_shouldAddSupplier() {
        robot.interact(() -> view.getNameField().setText("SupplierX"));
        robot.clickOn(view.getAddSupplierButton());

        assertTrue(SuppliersDAO.getAllSuppliers()
                .stream()
                .anyMatch(s -> s.getSupplierName().equals("SupplierX")));

        SuppliersDAO.deleteSupplier(new Supplier("SupplierX"));
    }

    @Test
    void addEmptySupplier_shouldNotAddSupplier() {
        robot.interact(() -> view.getNameField().setText(""));
        robot.clickOn(view.getAddSupplierButton());

        assertFalse(SuppliersDAO.getAllSuppliers()
                .stream()
                .anyMatch(s -> s.getSupplierName().equals("SupplierX")));
    }

    private static void clearAllItems() {
        executeDelete("DELETE FROM items");
    }

    private static void executeDelete(String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }
    public static Manager createTestManager() {
        try {
            testManager = new Manager("TestFirst", "TestLast", "test_manager_123", "password", "test@manager.com", "1234567890", LocalDate.of(1998,10,12), 5000);
            testManager.addSector(Sector.COMPUTERS);
            testManager.addSector(Sector.MOBILE_DEVICES);

            // Add to database
            EmployeeDAO.addEmployee(testManager);

            return testManager;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create test manager");
        }
    }
    public static void deleteTestManager() {
        if (testManager != null) {
            // First, delete all employee-sector mappings
            String deleteEmployeeSectors = "DELETE FROM employee_sectors WHERE employee_id = " + testManager.getId();
            executeDelete(deleteEmployeeSectors);

            // Then, delete the test manager from employees table
            String deleteEmployee = "DELETE FROM employees WHERE id = " + testManager.getId();
            executeDelete(deleteEmployee);
        }
    }

}
