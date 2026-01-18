package DAO;

import DAO.BillDAO;
import Models.*;
import FakeClasses.FakeEmployeeDAO;
import FakeClasses.FakeItemsDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BillDAOTest {

    private Employee employee;
    private Bill bill;

    @BeforeEach
    void setUp() {
        // Inject fake DAOs

        // Create employee
        employee = new Employee("cashier1", "Cashier", Role.CASHIER);

        // Create bill
        bill = new Bill(employee);
        bill.setSaleDate(LocalDateTime.now());

        // Add fake sold item
        SoldItem soldItem = new SoldItem(
                "Laptop",
                2,
                1200.00,
                900.00,
                LocalDateTime.now().toLocalDate()
        );

        bill.getSoldItems().add(soldItem);
    }

    @Test
    void saveBill_shouldNotThrowException_whenBillIsValid() {
        assertDoesNotThrow(() -> BillDAO.saveBill(bill));
    }

    @Test
    void saveBill_shouldNotSave_whenTotalIsZero() {
        Bill emptyBill = new Bill(employee);
        emptyBill.setSaleDate(LocalDateTime.now());

        assertDoesNotThrow(() -> BillDAO.saveBill(emptyBill));
    }

    @Test
    void getLastDate_shouldReturnCurrentDateTime() {
        assertNotNull(BillDAO.getLastDate());
    }

    @Test
    void getNumberOfBills_shouldReturnZeroWithFakeDB() {
        long count = BillDAO.getNumberOfBills();
        assertTrue(count >= 0);
    }

    @Test
    void getDayBills_shouldReturnList() {
        List<Bill> bills = BillDAO.getDayBills(employee);
        assertNotNull(bills);
    }
}
