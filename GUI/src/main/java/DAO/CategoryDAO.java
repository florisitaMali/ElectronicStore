package DAO;

import Models.Category;
import Models.Item;
import Models.Sector;
import Models.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public static ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        String sql = "SELECT *, sec.name AS sectori FROM categories JOIN sectors sec ON categories.sector = sec.id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getString("name"),
                        Sector.valueOf(rs.getString("sectori"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public static void addCategory(Category c) {
        String sql = "INSERT INTO categories(name, sector) VALUES (?, ?)";

        String sectorIdQuery = "SELECT id FROM sectors WHERE name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             PreparedStatement sectorPs = con.prepareStatement(sectorIdQuery)) {
            sectorPs.setString(1, c.getSector().name());
            ResultSet rs = sectorPs.executeQuery();
            if (rs.next()) {
                int sectorId = rs.getInt("id");
                ps.setString(1, c.getName());
                ps.setInt(2, sectorId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Category> getSectorCategory(List<Sector> sector) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE sector = ?";
        String sectorIdQuery = "SELECT id FROM sectors WHERE name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             PreparedStatement sectorPs = con.prepareStatement(sectorIdQuery)) {

            for (Sector s : sector) {
                sectorPs.setString(1, s.name());
                ResultSet rsSector = sectorPs.executeQuery();
                if (rsSector.next()) {
                    int sectorId = rsSector.getInt("id");
                    ps.setInt(1, sectorId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            categories.add(new Category(
                                    rs.getString("name"),
                                    s
                            ));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public static void deleteCategory(Category c) {
        String sql = "DELETE FROM categories WHERE name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Item> getSectorsItems(Sector se) {
        List<Item> items = new ArrayList<>();
        String sql = """
                    SELECT i.*, c.name AS category_name, c.sector, sec.name AS sectori
                    FROM items i
                    JOIN categories c ON i.category_id = c.id
                    JOIN sectors sec ON c.sector = sec.id
                    WHERE sec.name = ?
                """;
        System.out.println("SQL Query: " + sql); // Debugging line to check the SQL query

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, se.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category(
                            rs.getString("category_name"),
                            Sector.valueOf(rs.getString("sectori"))
                    );
                    System.out.println("Category fetched: " + category.getName() + ", Sector: " + category.getSector()); // Debugging line

                    Supplier supplier = SuppliersDAO.getSupplierById(rs.getInt("supplier_id"));
                    long stockLimit = rs.getLong("stock_limit");
                    double sellingPrice = rs.getDouble("selling_price");
                    double purchasedPrice = rs.getDouble("purchased_price");

                    System.out.println("Item fetched: " + rs.getString("name") + ", Quantity: " + rs.getInt("quantity")); // Debugging line
                    items.add(new Item(
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            category,
                            supplier,
                            purchasedPrice,
                            sellingPrice,
                            stockLimit
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
