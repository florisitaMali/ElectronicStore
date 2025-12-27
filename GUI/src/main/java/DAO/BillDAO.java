package DAO;

import Models.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class BillDAO {
    public static final File BILLS_FILE = new File("src/main/resources/com/example/gui/Bills.dat");

    public static ArrayList<Bill> getAllBills()
    {
        ArrayList<Bill> bills= new ArrayList<>();
        if (!BILLS_FILE.exists()) {
            return null;
        }

        try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(BILLS_FILE))) {
            LocalDateTime lastDate = (LocalDateTime) input.readObject();
            long nrOfBills = (long) input.readObject();
            bills = (ArrayList<Bill>) input.readObject();

        } catch (EOFException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return bills;
    }

    public static void addBillToFile(Bill bill)
    {
        ArrayList<Bill> bills = BillDAO.getAllBills();
        try(FileOutputStream billFile = new FileOutputStream(BILLS_FILE, false);
            ObjectOutputStream output= new ObjectOutputStream(billFile);
        )
        {
            if(BILLS_FILE.length() == 0)
            {
                output.writeObject(LocalDate.now());
                output.writeObject(0);
                output.writeObject(bills);
            }
            bills.add(bill);

            output.writeObject(bill.getSaleDate());
            output.writeObject(bill.getNumberOfBill());
            output.writeObject(bills);
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static LocalDateTime getLastDate()
    {
        LocalDateTime lastDate = LocalDateTime.now();
        try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(BILLS_FILE))) {
            lastDate = (LocalDateTime) input.readObject();
        } catch (EOFException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return lastDate;
    }

    public static long getNumberOfBills()
    {
        long nrOfBills = 0;
        try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(BILLS_FILE))) {
            LocalDateTime lastDate = (LocalDateTime) input.readObject();
            nrOfBills = (long) input.readObject();
        } catch (EOFException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return nrOfBills;
    }

    //Get Bill within a time period
    public static ArrayList<Bill> getAllBills(LocalDate startDate, LocalDate endDate) {
        ArrayList<Bill> bills;
        ArrayList<Bill> billsWithinTime = new ArrayList<>();
        if (!BILLS_FILE.exists()) {
            return null;
        }
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(BILLS_FILE))) {
            LocalDateTime lastDate = (LocalDateTime) input.readObject();
            long nrOfBills = (long) input.readObject();
            bills = (ArrayList<Bill>) input.readObject();

            for (Bill b : bills) {
                //Check if the date of bill generated is between the start and end date
                if ((b.getSaleDate().toLocalDate().isAfter(startDate) || b.getSaleDate().toLocalDate().isEqual(startDate)) && (b.getSaleDate().toLocalDate().isBefore(endDate) || b.getSaleDate().toLocalDate().isEqual(endDate)))
                    billsWithinTime.add(b);
            }
        } catch (EOFException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return billsWithinTime;
    }

    // Items sold statistics
    //Map<K,V> is an interface / K the type of key maintained by this map (item name)/ V the value to mapped on (the quantity)
    public static Map<String, Integer> getItemsSoldStatistics(LocalDate start, LocalDate end) {
        ArrayList<Bill> bills = getAllBills(start, end);
        //HashMap concrete implementation of Map
        Map<String, Integer> itemSales = new HashMap<>();

        if (bills != null) {
            for (Bill bill : bills) {
                ArrayList<SoldItem> items = bill.getSoldItems();
                for (Item item : items) {
                    String itemName = item.getItemName();
                    int quantity = item.getQuantity();
                    //Add the item into the map
                    itemSales.put(itemName, itemSales.getOrDefault(itemName, 0) + quantity);
                    //If the item already exist in map it will return its old quantity and add the quantity
                }
            }
        }
        return itemSales;
    }

    public static ArrayList<Bill> getDayBills(Employee emp) {
        ArrayList<Bill> temp = new ArrayList<>();
        ArrayList<Bill> bills = BillDAO.getAllBills();
        LocalDate today = LocalDate.now();

        int left = 0;
        int right = bills.size() - 1;

        //Binary Search
        while (left <= right) {
            int mid = (left + right) / 2;
            LocalDate dateMid = bills.get(mid).getSaleDate().toLocalDate();

            if (dateMid.isBefore(today)) {
                left = mid + 1;
            } else if (dateMid.isAfter(today)){
                right = mid - 1;
            } else {
                // Find all bills for the same date
                int i = mid;
                while (i >= 0 && bills.get(i).getSaleDate().toLocalDate().isEqual(today)) {
                    if (bills.get(i).getEmployee().equals(emp)) {
                        temp.add(bills.get(i));
                    }
                    i--;
                }
                i = mid + 1;
                while (i < bills.size() && bills.get(i).getSaleDate().toLocalDate().isEqual(today)) {
                    if (bills.get(i).getEmployee().equals(emp)) {
                        temp.add(bills.get(i));
                    }
                    i++;
                }
                break;
            }
        }

        for (Bill b : temp) {
            System.out.println(b.getBillNumber() + "  " + b.getSaleDate());
        }
        return temp;
    }

}
