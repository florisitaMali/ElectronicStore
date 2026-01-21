package Controller;

import Models.Role;
import Views.LoginView;
import Views.MainPageView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainPageController {
    private final MainPageView view;
    private final Stage stage;

    public MainPageController(MainPageView view, Stage stage) {
        this.stage = stage;
        this.view = view;
        enableButtons();
    }

    private void enableButtons() {
        setCashierButtonAction();
        setManagerButtonAction();
        setAdministratorButtonAction();
    }

    private void setCashierButtonAction() {
        view.getCashier().setOnAction(e -> {
            LoginView mainPage = new LoginView(Role.CASHIER);
            Stage stage = (Stage) view.getCashier().getScene().getWindow();
            Scene scene = new Scene(mainPage.getView(), 800, 500);
            stage.setScene(scene);
            stage.show();
        });
    }

    private void setManagerButtonAction() {
        view.getManager().setOnAction(e -> {
            LoginView mainPage = new LoginView(Role.MANAGER);
            Stage stage = (Stage) view.getManager().getScene().getWindow();
            Scene scene = new Scene(mainPage.getView(), 800, 500);
            stage.setScene(scene);
            stage.show();
        });
    }

    private void setAdministratorButtonAction() {
        view.getAdmin().setOnAction(e -> {
            System.out.println("Administrator button clicked");
            LoginView mainPage = new LoginView(Role.ADMINISTRATOR);
            Stage stage = (Stage) view.getAdmin().getScene().getWindow();
            Scene scene = new Scene(mainPage.getView(), 800, 500);
            stage.setScene(scene);
            stage.show();
        });
    }
}
