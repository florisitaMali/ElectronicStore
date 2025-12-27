module com.example.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.logging;
    requires java.compiler;

    exports com.example.gui;
    exports DAO;
    exports Views;
    exports Models;
    exports Main;
}
