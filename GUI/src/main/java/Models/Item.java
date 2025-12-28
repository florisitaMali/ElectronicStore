package Models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private int id;
    private String itemName;
    private int quantity;
    private Category category;
    private Supplier supplier;
    private LocalDate purchaseDate;
    private double purchasedPrice;
    private double sellingPrice;
    private Sector sector;
    private long stockLimit;

    public Item(){}

    public Item(String n, int q, Category c, Supplier s, double pp, double sp, long stockLimit) throws ItemNotAvailableException{
        this.itemName = n;
        setQuantity(q);
        this.category = c;
        this.supplier = s;
        this.purchaseDate = LocalDate.now(); // Defaults to the current date
        this.purchasedPrice = pp;
        this.sellingPrice = sp;
        this.sector = category.getSector();
        this.stockLimit = stockLimit;
    }

    // Getters
    public String getItemName() {
        return itemName;
    }
    public Sector getSector(){ return sector;}
    public int getQuantity() {
        return quantity;
    }
    public Category getItemCategory() {
        return category;
    }
    public Supplier getItemSupplier() {
        return supplier;
    }
    public LocalDate getPurchasedDate() {
        return purchaseDate;
    }
    public double getPurchasedPrice() {
        return purchasedPrice;
    }
    public double getSellingPrice() {
        return sellingPrice;
    }
    public long getStockLimit() { return stockLimit;}

    // Setters
    public void setItemName(String n) {
        this.itemName = n;
    }

    public void setQuantity(int q){
        if(q>=0) {
            this.quantity = q;
        }
    }

    public void setItemCategory(Category c) {
        this.category = c;
    }

    public void setItemSupplier(Supplier s) {
        this.supplier = s;
    }

    public void setPurchasedPrice(double p) {
        this.purchasedPrice = p;
    }

    public void setSellingPrice(double p) {
        this.sellingPrice = p;
    }

    public void setPurchasedDate(LocalDate date){ this.purchaseDate = date;}

    public void setStockLimit(long s){ this.stockLimit = s;}

    // Utility Method
    public boolean isOutOfStock() {
        return this.quantity < stockLimit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return getItemName();
    }
}