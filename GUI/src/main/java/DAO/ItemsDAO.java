package DAO;

import Models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemsDAO {

    public static ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<>();

        String sql = """
            SELECT i.*, c.name AS category_name, c.sector, sec.name AS sectori, s.name AS supplier_name, s.address AS supplier_address
            FROM items i
            JOIN categories c ON i.category_id = c.id
            JOIN suppliers s ON i.supplier_id = s.id
            JOIN sectors sec ON c.sector = sec.id
            WHERE i.deleted = FALSE
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category category = new Category(
                        rs.getString("category_name"),
                        Sector.valueOf(rs.getString("sectori"))
                );

                Supplier supplier = new Supplier(
                        rs.getString("supplier_name"),
                        rs.getString("supplier_address")
                );

                long stockLimit = rs.getLong("stock_limit");
                double sellingPrice = rs.getDouble("selling_price");
                double purchasedPrice = rs.getDouble("purchased_price");

                Item item = new Item(
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        category,
                        supplier,
                        purchasedPrice,
                        sellingPrice,
                        stockLimit
                );
                item.setId(rs.getInt("id"));
                items.add(item);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public static void addItem(Item item) {
        String checkIfExistsSql = "SELECT COUNT(*) FROM items WHERE name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkIfExistsSql)) {

            checkPs.setString(1, item.getItemName());
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Item with name '" + item.getItemName() + "' already exists.");
                    return;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        String sql = """
            INSERT INTO items(name, quantity, category_id, supplier_id, purchased_date, purchased_price, selling_price, stock_limit)
            VALUES (?, ?, (SELECT id FROM categories WHERE name = ?), (SELECT id FROM suppliers WHERE name = ?), ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getQuantity());
            ps.setString(3, item.getItemCategory().getName());
            ps.setString(4, item.getItemSupplier().getSupplierName());
            ps.setDate(5, Date.valueOf(item.getPurchasedDate()));
            ps.setDouble(6, item.getPurchasedPrice());
            ps.setDouble(7, item.getSellingPrice());
            ps.setLong(8, item.getStockLimit());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteItem(Item item) {
        String sql = "DELETE FROM items WHERE name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getItemName());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Item searchItem(String itemName) {
        String sql = """
            SELECT i.*, c.name AS category_name, c.sector, sec.name AS sectori, s.name AS supplier_name, s.address AS supplier_address
            FROM items i
            JOIN categories c ON i.category_id = c.id
            JOIN suppliers s ON i.supplier_id = s.id
            JOIN sectors sec ON c.sector = sec.id
            WHERE i.name LIKE ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + itemName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category(
                            rs.getString("category_name"),
                            Sector.valueOf(rs.getString("sectori"))
                    );

                    Supplier supplier = new Supplier(
                            rs.getString("supplier_name"),
                            rs.getString("supplier_address")
                    );

                    return new Item(
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            category,
                            supplier,
                            rs.getDouble("purchased_price"),
                            rs.getDouble("selling_price"),
                            rs.getLong("stock_limit")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, Integer> getItemsPurchasedStatistics(
            LocalDate startDate,
            LocalDate endDate
    ) {

        Map<String, Integer> result = new HashMap<>();

        String sql = """
            SELECT i.name AS item_name, SUM(bi.quantity) AS total_quantity
            FROM bill_items bi
            JOIN items i ON bi.item_id = i.id
            JOIN bills b ON bi.bill_id = b.id
            WHERE b.date BETWEEN ? AND ?
            GROUP BY i.name
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.put(
                        rs.getString("item_name"),
                        rs.getInt("total_quantity")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int getItemId(SoldItem item){
        String sql = "SELECT id FROM items WHERE name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getItemName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void updateItem(Item item) {

        String sql = """
        UPDATE items
        SET name = ?,
            quantity = ?,
            category_id = (SELECT id FROM categories WHERE name = ?),
            supplier_id = (SELECT id FROM suppliers WHERE name = ?),
            purchased_price = ?,
            selling_price = ?,
            stock_limit = ?
        WHERE id = ?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getQuantity());
            ps.setString(3, item.getItemCategory().getName());
            ps.setString(4, item.getItemSupplier().getSupplierName());
            ps.setDouble(5, item.getPurchasedPrice());
            ps.setDouble(6, item.getSellingPrice());
            ps.setLong(7, item.getStockLimit());
            ps.setInt(8, item.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void softDeleteItem(Item item) {
        String sql = "UPDATE items SET deleted = TRUE WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
