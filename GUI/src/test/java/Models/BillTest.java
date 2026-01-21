package Models;

import DAO.*;
import FakeClasses.FakeBill;
import FakeClasses.FakeBillDAO;
import FakeClasses.FakeEmployeeDAO;
import FakeClasses.FakeSoldItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BillTest {

    private Employee admin = EmployeeDAO.getAdministrator();
    private Employee otherEmployee;

    @BeforeEach
    void setUp() {
        admin = EmployeeDAO.getAdministrator();
        otherEmployee = new Cashier();
        try {
            Category testCategory = new Category("Electronics", Sector.ELECTRONICS);
            CategoryDAO.addCategory(testCategory);
            Supplier testSupplier = new Supplier("BestSupplier", "123 Supplier St.");
            SuppliersDAO.addSupplier(testSupplier);
            Item item = new Item("TEST_ITEM", 10, testCategory, testSupplier, 500, 700, 50);
            ItemsDAO.addItem(item);
        }catch (Exception e){

        }
    }


    @Test
    void addSoldItems_shouldDoNothing() {
        Bill bill = new Bill();
        bill.addSoldItems(new FakeSoldItem("X", 1, 10));

        assertEquals(0, bill.getSoldItems().size());
    }

    @Test
    void deleteSoldItem_shouldDoNothing() {
        Bill bill = new Bill();
        bill.deleteSoldItem(new FakeSoldItem("I1", 2, 30));
        assertEquals(0, bill.getSoldItems().size());
    }

    @Test
    void getBillsCost_shouldReturnFakeValue() {
        Bill bill = new Bill();
        bill.addSoldItems(new FakeSoldItem("TEST_ITEM", 2, 30.0));

        // FakeSoldItem returns cost of 10.0 per item
        double cost = bill.getBillsCost(bill, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertEquals(20.0, cost);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDatesForCost")
    void getBillsCost_invalidDates_throwsException(LocalDate start, LocalDate end) {
        Bill bill = new Bill();
        assertThrows(IllegalArgumentException.class, () -> bill.getBillsCost(bill, start, end));
    }

    static Stream<Arguments> provideInvalidDatesForCost() {
        return Stream.of(Arguments.of(null, LocalDate.now().plusDays(1)), Arguments.of(LocalDate.now().minusDays(1), null), Arguments.of(LocalDate.now().plusDays(1), LocalDate.now()));
    }


    @Test
    void getBills_shouldReturnListWithOneFakeBill() {
        Bill bill = new Bill(admin);
        bill.addSoldItems(new SoldItem("TEST_ITEM", 1));
        BillDAO.setEmployeeDAO(new FakeEmployeeDAO());
        BillDAO.saveBill(bill);

        ArrayList<Bill> bills = bill.getBills(admin, LocalDate.now().minusDays(1), LocalDate.now());
        assertEquals(1, bills.size());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDatesForBills")
    void getBills_invalidDates_throwsException(LocalDate start, LocalDate end) {
        Bill bill = new Bill();
        assertThrows(IllegalArgumentException.class, () -> bill.getBills(admin, start, end));
    }

    static Stream<Arguments> provideInvalidDatesForBills() {
        return Stream.of(Arguments.of(null, LocalDate.now()),
                Arguments.of(LocalDate.now(), null),
                Arguments.of(LocalDate.now().plusDays(1), LocalDate.now()));
    }

}
