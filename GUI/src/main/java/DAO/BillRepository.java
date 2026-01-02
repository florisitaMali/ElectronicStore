package DAO;

import Models.Bill;

import java.time.LocalDate;
import java.util.ArrayList;

public interface BillRepository {
    ArrayList<Bill> getAllBills(LocalDate start, LocalDate end);
}
