module com.example.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.compiler;
    requires java.sql;
    requires javafx.graphics;
    requires mysql.connector.j;

    exports com.example.gui;
    exports DAO;
    exports Views;
    exports Models;
    exports Main;
}
