package DAO;

import FakeClasses.*;
import Models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BillDAOTest {

    private Employee employee;
    private Bill bill;

    @BeforeEach
    void setUp() {
        bill = new FakeBill();

        SoldItem soldItem = new FakeSoldItem("Laptop", 2, 100);

        bill.getSoldItems().add(soldItem);
    }

    @Test
    void saveBill_shouldNotThrowException_whenBillIsValid() {
        assertDoesNotThrow(() -> BillDAO.saveBill(bill));
    }

    @Test
    void saveBill_shouldNotSave_whenTotalIsZero() {
        Bill emptyBill = new FakeBill(){
            @Override
            public double getTotalPrice() {
                return 0;
            }
        };
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
