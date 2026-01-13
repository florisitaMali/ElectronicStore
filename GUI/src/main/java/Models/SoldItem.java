package Models;

import DAO.ItemsDAO;
import DAO.ItemsDAOAdapter;
import DAO.ItemsRepository;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class SoldItem extends Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private int soldQuantity;
    private static ItemsRepository itemsDAO = new ItemsDAOAdapter();

    public static void setItemsDAO(ItemsRepository dao) {
        itemsDAO = dao;
    }

    public SoldItem(String n, int sq) throws ItemNotFoundException, ItemNotAvailableException {
        Item i = itemsDAO.searchItem(n);
        System.out.println(i.getItemName() + "  " + i.getQuantity() + " " + sq);

        //Ensure that there is enough quantity available
        if (i.getQuantity() < sq) {
            throw new ItemNotAvailableException("Item not available: " + i.getItemName() + "\nQuantity avalable: " + i.getQuantity() + "\n Quantity requested: " + sq);
        }

        setItemName(i.getItemName());
        setItemCategory(i.getItemCategory());
        setItemSupplier(i.getItemSupplier());
        setPurchasedDate(i.getPurchasedDate());
        setPurchasedPrice(i.getPurchasedPrice());
        setSellingPrice(i.getSellingPrice());

        soldQuantity = sq;
    }

    public SoldItem(){}

    public SoldItem(
            String name,
            int soldQty,
            double sellingPrice,
            double purchasedPrice,
            LocalDate purchasedDate
    ) {
        setItemName(name);
        setSellingPrice(sellingPrice);
        setPurchasedPrice(purchasedPrice);
        setPurchasedDate(purchasedDate);
        this.soldQuantity = soldQty;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int sq) throws ItemNotAvailableException {
        int newQuantity = getQuantity() - sq;

        if (newQuantity <= 1) {
            throw new ItemNotAvailableException("There is not enough stock for this item.\nThere are only " + getQuantity() + " items in stock.");
        }
        setQuantity(newQuantity);
        soldQuantity = sq;
        System.out.println(soldQuantity);
    }
}

