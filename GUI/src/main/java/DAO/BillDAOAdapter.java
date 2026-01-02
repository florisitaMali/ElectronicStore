package DAO;

import Models.Bill;

import java.time.LocalDate;
import java.util.ArrayList;

public class BillDAOAdapter implements BillRepository {
    @Override
    public ArrayList<Bill> getAllBills(LocalDate start, LocalDate end) {
        return BillDAO.getAllBills(start, end); // call static
    }
}