package Models;

public class LowStockItem extends RuntimeException {
    public LowStockItem(String message) {
        super(message);
    }
}
