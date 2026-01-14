package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/electronic_store?useSSL=false&serverTimezone=UTC&autoReconnect=true";
    private static final String USER = "root"; // replace with your real user
    private static final String PASSWORD = ""; // replace with your real password

    // For unit testing / injection
    private static Connection testConnection;


    public static void setConnection(Connection conn) {
        testConnection = conn;
    }

    public static Connection getConnection() throws SQLException {
        try {
            return testConnection != null ? testConnection : DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }
}
