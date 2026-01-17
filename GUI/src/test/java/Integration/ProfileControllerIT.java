package Integration;

import Controller.ProfileController;
import DAO.EmployeeDAO;
import Models.Employee;
import Views.Profile;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileControllerIT extends ApplicationTest {

    private Profile profile;

    @Override
    public void start(Stage stage) {
        Employee admin = EmployeeDAO.getAdministrator();
        assertNotNull(admin, "Administrator must exist in DB");

        profile = new Profile(admin);

        Scene scene = new Scene(profile.getView(), 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void testProfileNavigationButtons() {
        // Personal Details
        clickOn(profile.getPersonalDetails());
        assertEquals(profile.getPersonalInfo(),
                profile.getMainPane().getCenter());

        // Work Related Details
        clickOn(profile.getWorkRelatedDetails());
        assertEquals(profile.getOtherInfo(),
                profile.getMainPane().getCenter());

        // Security Info
        clickOn(profile.getUsernamePassWord());
        assertEquals(profile.getSecurityInfo(),
                profile.getMainPane().getCenter());
    }

    @Test
    void testSecuritySectionShowsUsername() {
        clickOn(profile.getUsernamePassWord());
        assertNotNull(profile.getUsername().getText());
        assertFalse(profile.getUsername().getText().isEmpty());
    }
}
