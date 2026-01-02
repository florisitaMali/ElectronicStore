package FakeClasses;
import Models.Category;
import Models.Item;
import Models.Sector;
import Models.Supplier;

import java.time.LocalDate;

// FakeItem for unit tests that don't depend on real Item logic
public class FakeItem extends Item {
    private String name;
    private int quantity;
    private boolean deleted;

    public FakeItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.deleted = false;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public String getItemName() {
        return name;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    @Override
    public Sector getSector(){ return Sector.CAMERA;}
    @Override
    public Category getItemCategory() {
        return new FakeCategory("FakeCategory", Sector.CAMERA);
    }
    @Override
    public Supplier getItemSupplier() {
        return new FakeSupplier("FakeSupplier");
    }
    @Override
    public LocalDate getPurchasedDate() {
        return LocalDate.of(2025, 12, 20);
    }
    @Override
    public double getPurchasedPrice() {
        return 100;
    }
    @Override
    public double getSellingPrice() {
        return 120;
    }
    @Override
    public long getStockLimit() { return 10;}

}
