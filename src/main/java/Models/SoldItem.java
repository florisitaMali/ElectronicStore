package Models;

import DAO.ItemsDAO;

import java.io.Serial;
import java.io.Serializable;

public class SoldItem extends Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private int soldQuantity;

    public SoldItem(String n, int sq) throws ItemNotFoundException, ItemNotAvailableException {
        Item i = ItemsDAO.searchItem(n);
        System.out.println(i.getItemName() + "  " + i.getQuantity() + " " + sq);

        //Ensure that there is enough quantity available
        if (i.getQuantity() < sq) {
            throw new ItemNotAvailableException("Item not available: " + i.getItemName() + "\nQuantity avalable: " + i.getQuantity() + "\n Quantity requested: " + sq);
        }

        //Update the item quantity
        setQuantity(i.getQuantity());
        i.setQuantity(i.getQuantity() - sq);

        //Initialize SoldItem properties
        setItemName(i.getItemName());
        setItemCategory(i.getItemCategory());
        setItemSupplier(i.getItemSupplier());
        setPurchasedDate(i.getPurchasedDate());
        setPurchasedPrice(i.getPurchasedPrice());
        setSellingPrice(i.getSellingPrice());

        //Set the sold quantity
        setSoldQuantity(sq);
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int sq) throws ItemNotAvailableException {
        int newQuantity = getQuantity() - sq;

        if (newQuantity < 0) {
            throw new ItemNotAvailableException("There is not enough stock for this item.\nThere are only " + getQuantity() + " items in stock.");
        }
        setQuantity(newQuantity);
        soldQuantity = sq;
        System.out.println(soldQuantity);
    }
}

