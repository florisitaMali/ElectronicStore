package Models;

import FakeClasses.FakeCashier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CashierTest {

    private FakeCashier cashier;

    @BeforeEach
    void setUp() throws NotValidUsername {
        cashier = new FakeCashier("Bob", "Johnson", "cashier01", "password123", "bob@company.com", "9876543210", LocalDate.of(1990, 8, 15), 2500.0, Sector.HOME_ENTERTAINMENT);
    }

    @Test
    void defaultConstructor_shouldSetRoleCashier() {
        FakeCashier defaultCashier = new FakeCashier();
        assertEquals(Role.CASHIER, defaultCashier.getRole());
    }

    @Test
    void parameterizedConstructor_shouldSetRoleCashier() {
        assertEquals(Role.CASHIER, cashier.getRole());
    }

    @Test
    void getSector_shouldReturnCorrectSector() {
        assertEquals(Sector.HOME_ENTERTAINMENT, cashier.getSector());
    }

    @Test
    void setSector_shouldChangeSector() {
        cashier.setSector(Sector.COMPUTERS);
        assertEquals(Sector.COMPUTERS, cashier.getSector());
    }

    @Test
    void toString_shouldContainRoleCashier() {
        String result = cashier.toString();
        assertTrue(result.contains("Role: Cashier"));
    }
}
