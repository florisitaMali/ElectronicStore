package FakeClasses;

import Models.Cashier;
import Models.Sector;
import Models.NotValidUsername;

/**
 * FakeCashier is a mock version of Cashier for testing.
 * It avoids any external dependencies and keeps sector in memory.
 */
public class FakeCashier extends Cashier {

    // Default constructor
    public FakeCashier() {
        super(); // calls Cashier default constructor
    }

    // Parameterized constructor
    public FakeCashier(String name, String surname, String username, String psw,
                       String email, String phoneNr, java.time.LocalDate dateOfBirth,
                       double salary, Sector sector) throws NotValidUsername {
        super(name, surname, username, psw, email, phoneNr, dateOfBirth, salary, sector);
    }

    @Override
    public void setSector(Sector s) {
        // Override if needed for testing purposes
        super.setSector(s);
    }

    @Override
    public Sector getSector() {
        return super.getSector();
    }
}
