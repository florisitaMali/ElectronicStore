package DAO;

import DAO.*;
import FakeClasses.FakeCashier;
import FakeClasses.FakeEmployeeDAO;
import FakeClasses.FakeItemsDAO;
import FakeClasses.FakeSoldItem;
import Models.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BillDAOTest {

    private EmployeeRepository employeeDAO;
    private ItemsRepository itemsDAO;

    @BeforeEach
    void setup() {
        employeeDAO = new FakeEmployeeDAO();
        itemsDAO = new FakeItemsDAO();

        BillDAO.setEmployeeDAO(employeeDAO);
        BillDAO.setItemsDAO(itemsDAO);
    }

    @Test
    void saveBill_totalZero_doesNothing() {
        Employee e = new FakeCashier();
        Bill bill = new Bill(e);
        int before = BillDAO.getAllBills().size();
        assertDoesNotThrow(() -> BillDAO.saveBill(bill));
        assertEquals(before, BillDAO.getAllBills().size());
    }

    @Test
    void saveBill_withTotalNotZero() {
        int before = BillDAO.getAllBills().size();
        Administrator e = EmployeeDAO.getAdministrator();
        Bill bill = new Bill(e);
        bill.addSoldItems(new FakeSoldItem("TEST_ITEM", 1, 100));

        assertDoesNotThrow(() -> BillDAO.saveBill(bill));
        assertEquals(before + 1, BillDAO.getAllBills().size());
    }

    @ParameterizedTest
    @CsvSource({
            "2024-01-01,2024-12-31",
            "2025-01-01,2025-01-01"
    })
    void getAllBills_withDateRange_doesNotFail(
            LocalDate start,
            LocalDate end) {

        List<Bill> bills = BillDAO.getAllBills(start, end);
        assertNotNull(bills);
    }

    @Test
    void getAllBills_emptyDatabase_returnsEmptyList() {
        deleteDatabase();
        List<Bill> bills = BillDAO.getAllBills();
        assertTrue(bills.isEmpty());
    }


    @ParameterizedTest
    @MethodSource("dateRanges")
    void getItemsSoldStatistics_noData_returnsEmptyMap(LocalDate start, LocalDate end) {

        Map<String, Integer> stats =
                BillDAO.getItemsSoldStatistics(start, end);

        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

    static Stream<Arguments> dateRanges() {
        return Stream.of(
                Arguments.of(LocalDate.now(), LocalDate.now()),
                Arguments.of(LocalDate.now(), LocalDate.now().plusDays(30))
        );
    }

    @ParameterizedTest
    @MethodSource("dateRangesValid")
    void getItemsSoldStatistics_validDate(LocalDate start, LocalDate end) {

        Map<String, Integer> stats =
                BillDAO.getItemsSoldStatistics(start, end);

        assertNotNull(stats);
        assertFalse(stats.isEmpty());
    }

    static Stream<Arguments> dateRangesValid() {
        return Stream.of(
                Arguments.of(LocalDate.now().minusDays(30), LocalDate.now()),
                Arguments.of(LocalDate.now().minusDays(1), LocalDate.now())
        );
    }

    @Test
    void getNumberOfBills_returnsZeroWhenNoBills() {
        long count = BillDAO.getNumberOfBills();
        assertTrue(count >= 0);
    }

    @Test
    void getNumberOfBills_returnNumWhenBills() {
        deleteDatabase();
        Bill bill = new Bill(EmployeeDAO.getAdministrator());
        bill.addSoldItems(new SoldItem("TEST_ITEM", 1));
        BillDAO.saveBill(bill);
        long count = BillDAO.getNumberOfBills();
        assertTrue(count > 0);
    }


    @Test
    void getDayBills_noBills_returnsEmptyList() {
        Employee e = new FakeCashier();
        List<Bill> bills = BillDAO.getDayBills(e);
        assertTrue(bills.isEmpty());
    }


    @Test
    void getLastDate_returnsNowOrLater() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime returned = BillDAO.getLastDate();

        assertFalse(returned.isBefore(now.minusSeconds(1)));
    }

    private void deleteDatabase(){
        String sql = "DELETE FROM bills";
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
        ){
            ps.executeUpdate();
        }catch (Exception ex){

        }
    }
}
