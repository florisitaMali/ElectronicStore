package Main;

import Controller.MainPageController;
import Views.MainPageView;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainPageView mainPage = new MainPageView();
        new MainPageController(mainPage, primaryStage);
        Scene scene = new Scene(mainPage.getView(), 800, 500);
        primaryStage.setTitle("Electronic Store");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

}
