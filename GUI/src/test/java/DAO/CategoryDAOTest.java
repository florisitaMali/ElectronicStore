package DAO;

import Models.Category;
import Models.Item;
import Models.Sector;
import Models.Supplier;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDAOTest {

    @BeforeEach
    void setup() {
        deleteCategories();
        deleteItems();
        deleteSupplier();
    }

    @Test
    void getCategories_emptyDatabase_returnsEmptyList() {
        List<Category> categories = CategoryDAO.getCategories();
        assertNotNull(categories);
        assertTrue(categories.isEmpty());
    }

    @Test
    void getCategories_withData_returnsCategories() {
        Category c1 = new Category("Electronics", Sector.ELECTRONICS);
        CategoryDAO.addCategory(c1);

        List<Category> categories = CategoryDAO.getCategories();
        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals("Electronics", categories.get(0).getName());
        assertEquals(Sector.ELECTRONICS, categories.get(0).getSector());
    }


    @ParameterizedTest
    @EnumSource(Sector.class)
    void addCategory_validCategory_addsSuccessfully(Sector sector) {
        String categoryName = "Test_" + sector.name();
        Category c = new Category(categoryName, sector);

        assertDoesNotThrow(() -> CategoryDAO.addCategory(c));

        List<Category> categories = CategoryDAO.getCategories();
        assertTrue(categories.stream().anyMatch(cat -> cat.getName().equals(categoryName)));
    }


    @Test
    void getSectorCategory_noData_returnsEmptyList() {
        List<Category> categories = CategoryDAO.getSectorCategory(List.of(Sector.ELECTRONICS));
        assertNotNull(categories);
        assertTrue(categories.isEmpty());
    }

    @Test
    void getSectorCategory_withData_returnsOnlyMatchingSector() {
        CategoryDAO.addCategory(new Category("Laptop", Sector.ELECTRONICS));
        CategoryDAO.addCategory(new Category("Camera", Sector.CAMERA));

        List<Category> categories = CategoryDAO.getSectorCategory(List.of(Sector.ELECTRONICS));
        assertEquals(1, categories.size());
        assertEquals(Sector.ELECTRONICS, categories.get(0).getSector());
    }


    @Test
    void deleteCategory_existingCategory_removesCategory() {
        Category c = new Category("DeleteMe", Sector.ELECTRONICS);
        CategoryDAO.addCategory(c);

        assertDoesNotThrow(() -> CategoryDAO.deleteCategory(c));

        List<Category> categories = CategoryDAO.getCategories();
        assertTrue(categories.stream().noneMatch(cat -> cat.getName().equals("DeleteMe")));
    }

    @Test
    void deleteCategory_nonExistingCategory_doesNotThrow() {
        Category c = new Category("NonExist", Sector.CAMERA);
        assertDoesNotThrow(() -> CategoryDAO.deleteCategory(c));
    }


    @ParameterizedTest
    @EnumSource(Sector.class)
    void getSectorsItems_noItems_returnsEmptyList(Sector sector) {
        List<Item> items = CategoryDAO.getSectorsItems(sector);
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void getSectorsItems_itemsExistButNotInSector_returnsEmptyList() {
        Supplier s = new Supplier("Supplier1", "Address1");
        SuppliersDAO.addSupplier(s);

        Category foodCategory = new Category("Food", Sector.COMPUTERS);
        CategoryDAO.addCategory(foodCategory);

        addItem("Apple", 20, foodCategory, s);

        List<Item> items = CategoryDAO.getSectorsItems(Sector.ELECTRONICS);

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void getSectorsItems_noCategories_returnsEmptyList() {
        List<Item> items = CategoryDAO.getSectorsItems(Sector.ELECTRONICS);

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void getSectorsItems_withItems_returnsItems() {
        Supplier s = new Supplier("Supplier1", "Address1");
        SuppliersDAO.addSupplier(s);

        Category c = new Category("Electronics", Sector.ELECTRONICS);
        CategoryDAO.addCategory(c);

        addItem("Laptop", 10, c, s);

        List<Item> items = CategoryDAO.getSectorsItems(Sector.ELECTRONICS);
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Laptop", items.get(0).getItemName());
        assertEquals(Sector.ELECTRONICS, items.get(0).getItemCategory().getSector());
    }

    // Helpers

    private void deleteCategories() {
        String sql = "DELETE FROM categories";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private void deleteItems() {
        String sql = "DELETE FROM items";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private void deleteSupplier(){
        String sql = "DELETE FROM suppliers";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private void addItem(String name, int quantity, Category category, Supplier supplier) {
        String sql = "INSERT INTO items(name, quantity, category_id, supplier_id, purchased_price, purchased_date, selling_price, stock_limit) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int categoryId = getCategoryIdByName(category.getName());
            int supplierId = getSupplierIdByName(supplier.getSupplierName());

            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, categoryId);
            ps.setInt(4, supplierId);
            ps.setDouble(5, 100); // purchased_price
            ps.setDate(6, Date.valueOf(LocalDate.now()));
            ps.setDouble(7, 150); // selling_price
            ps.setInt(8, 50); // stock_limit
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCategoryIdByName(String name) {
        String sql = "SELECT id FROM categories WHERE name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception ignored) {}
        return 0;
    }

    private int getSupplierIdByName(String name) {
        String sql = "SELECT id FROM suppliers WHERE name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception ignored) {}
        return 0;
    }
}
