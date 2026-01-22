package FakeClasses;

import java.time.LocalDate;
import java.util.ArrayList;
import Models.Manager;
import Models.Sector;
import Models.NotValidUsername;

/**
 * FakeManager is a mock version of Manager for testing purposes.
 * It avoids DB calls or other side effects and stores sectors locally.
 */
public class FakeManager extends Manager {

    private final ArrayList<Sector> sectors = new ArrayList<>();

    public FakeManager() {
        super(); // uses Manager default constructor
    }

    // Parameterized constructor
    public FakeManager(String name, String surname, String username, String psw,
                       String email, String phoneNr, LocalDate dateOfBirth, double salary)
            throws NotValidUsername {
        super(name, surname, username, psw, email, phoneNr, dateOfBirth, salary);
    }

    @Override
    public void addSector(Sector s) {
        // Store sectors locally instead of calling DB or other logic
        sectors.add(s);
    }

    @Override
    public ArrayList<Sector> getSectors() {
        return sectors;
    }
}
