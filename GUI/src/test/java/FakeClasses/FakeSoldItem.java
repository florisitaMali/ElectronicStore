package FakeClasses;

import Models.SoldItem;

import java.time.LocalDate;

public class FakeSoldItem extends SoldItem {
    private int soldQuantity;
    private String itemName;
    private double sellingPrice;

    public FakeSoldItem(String name, int soldQty, double sellingPrice) {

        this.itemName = name;
        this.soldQuantity = soldQty;
        this.sellingPrice = sellingPrice;
    }

    @Override
    public int getSoldQuantity() {
        return soldQuantity;
    }

    @Override
    public void setSoldQuantity(int sq) {
        soldQuantity = sq;
    }
}
