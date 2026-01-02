package DAO;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/electronic_store";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    public static Connection connection;

    private static Connection testConnection;

    public static void setConnection(Connection conn) {
        testConnection = conn;
    }

    public static Connection getConnection() throws SQLException {
        return testConnection != null ? testConnection : DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
