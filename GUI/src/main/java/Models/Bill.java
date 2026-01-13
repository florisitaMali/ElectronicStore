package Models;
import DAO.BillDAO;
import DAO.ItemsDAO;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Bill implements Serializable{
    @Serial
    private static final long serialVersionUID = 0L;

    private long billNumber;
    private ArrayList<SoldItem> soldItems = new ArrayList<>();
    private double totalPrice;
    private LocalDateTime saleDate;
    private Employee employee;

    public Bill(){}

    public Bill(Employee emp)
    {
        //Each bill produced at a particular date
        saleDate = LocalDateTime.now();
        billNumber = BillDAO.getNumberOfBills();
        //When the bill is created and there is no item, total price is 0
        totalPrice = 0;
        this.employee = emp;
    }

    //Getters
    public long getBillNumber(){ return billNumber;}
    public ArrayList<SoldItem> getSoldItems(){ return soldItems;}
    public double getTotalPrice(){ return totalPrice;}
    public LocalDateTime getSaleDate(){ return saleDate;}
    public Employee getEmployee(){return employee;}

    //Managing sold items
    public void addSoldItems(SoldItem s) throws ItemNotFoundException
    {
        for (int i = 0; i < soldItems.size(); i++) {
            if (soldItems.get(i).getItemName().equals(s.getItemName())) {
                // Subtract the price of the existing item from the total price before deleting it
                totalPrice -= soldItems.get(i).getSellingPrice() * soldItems.get(i).getQuantity();
                deleteSoldItem(soldItems.get(i));
            }
        }

        updateFile(s);
        soldItems.add(s);
        totalPrice += s.getSellingPrice() * s.getSoldQuantity();
    }

    public void deleteSoldItem(SoldItem item) throws ItemNotFoundException
    {
        for(SoldItem s: soldItems)
        {
            if(item.getItemName().equals(s.getItemName()))
            {
                Item i = ItemsDAO.searchItem(item.getItemName());
                ItemsDAO.deleteItem(i);
                //Change the quantity of the item
                i.setQuantity(i.getQuantity() + s.getSoldQuantity());
                ItemsDAO.addItem(i);
                soldItems.remove(s);
                totalPrice -= s.getSoldQuantity() * s.getSellingPrice();
                return;
            }
        }
        System.out.println("There is no such item!");
    }

    private void updateFile(SoldItem s) throws ItemNotAvailableException
    {
        Item i = ItemsDAO.searchItem(s.getItemName());
        ItemsDAO.deleteItem(i);
        i.setQuantity(i.getQuantity() - s.getSoldQuantity());
        ItemsDAO.addItem(i);
    }

    public String printBill() {

        return String.format("%-20s %d\n", "Bill Number:", billNumber) +
                String.format("%-20s %s\n", "Sale Date:", saleDate.format(DateTimeFormatter.ofPattern("d/M/yyyy"))) +
                String.format("%-20s %s\n\n", "Sale Time:", saleDate.format(DateTimeFormatter.ofPattern("H:mm"))) +
                ("-".repeat(60)) + "\n" +
                String.format("|%-20s|%-10s|%10s|%20s|\n", "Item Name:", "Quantity", "Price", "TotalPrice") +
                ("-".repeat(60)) + "\n" +
                ItemPrint() +
                String.format("%s%.3f", "TOTAL FOR " + soldItems.size() + " ITEMS ", totalPrice);
    }

    public String ItemPrint()
    {
        String str = new String();
        for(SoldItem s: soldItems)
        {
            str = str + String.format("|%-20s|%-10d|%10.3f|%20.3f|\n", s.getItemName(), s.getSoldQuantity(), s.getSellingPrice(), s.getSoldQuantity()*s.getSellingPrice())
                    + ("-".repeat(60)) + "\n";
        }
        return str;
    }

    public double getBillsCost(Bill b, LocalDate startDate, LocalDate endDate)
    {
        double totalCost = 0;
        //Gets the cost of the items sold
        ArrayList<SoldItem> items = b.getSoldItems();
        for(SoldItem s: items)
        {
            //Check if the items was bought between these dates
            if((s.getPurchasedDate().isAfter(startDate) || s.getPurchasedDate().isEqual(startDate)) && (s.getPurchasedDate().isBefore(endDate) || s.getPurchasedDate().isEqual(startDate)))
                totalCost += s.getPurchasedPrice() * s.getSoldQuantity();
        }
        return totalCost;
    }

    public ArrayList<Bill> getBills(Employee emp, LocalDate start, LocalDate end)
    {
        //Get all bills between these dates
        ArrayList<Bill> bills = BillDAO.getAllBills(start, end);
        ArrayList<Bill> temp = new ArrayList<>();
        //Get all bills produced
        for(Bill b: bills)
        {
            if(b.getEmployee().getUsername().equals(emp.getUsername()))
            {
                temp.add(b);
            }
        }
        return temp;
    }
}
