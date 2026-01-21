package Models;

import FakeClasses.FakeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class ManagerTest {

    private FakeManager manager;

    @BeforeEach
    void setUp() throws NotValidUsername {
        manager = new FakeManager("Alice", "Smith", "manager01", "password123", "alice@company.com", "1234567890", LocalDate.of(1985, 5, 20), 4000.0);
    }

    @Test
    void defaultConstructor_shouldSetRoleManager() {
        FakeManager defaultManager = new FakeManager();
        assertEquals(Role.MANAGER, defaultManager.getRole());
    }

    @Test
    void parameterizedConstructor_shouldSetRoleManager() {
        assertEquals(Role.MANAGER, manager.getRole());
    }

    @Test
    void addSector_shouldAddSectorToList() {
        manager.addSector(Sector.COMPUTERS);
        manager.addSector(Sector.MOBILE_DEVICES);

        ArrayList<Sector> sectors = manager.getSectors();
        assertEquals(2, sectors.size());
        assertTrue(sectors.contains(Sector.COMPUTERS));
        assertTrue(sectors.contains(Sector.MOBILE_DEVICES));
    }

    @Test
    void getSectors_shouldReturnEmptyListInitially() {
        FakeManager emptyManager = new FakeManager();
        assertTrue(emptyManager.getSectors().isEmpty());
    }
}
