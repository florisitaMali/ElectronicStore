module com.example.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.compiler;
    requires java.sql;

    exports com.example.gui;
    exports DAO;
    exports Views;
    exports Models;
    exports Main;
}
