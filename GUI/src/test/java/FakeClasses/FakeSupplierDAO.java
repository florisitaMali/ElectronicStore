package FakeClasses;

import java.util.ArrayList;
import java.util.List;

public class FakeSupplierDAO {
    private static ArrayList<FakeSupplier> suppliers;

    public FakeSupplierDAO(){
        suppliers = new java.util.ArrayList<>();
        FakeSupplier fakeSupplier1 = new FakeSupplier("Supplier A", "123 Main St");
        FakeSupplier fakeSupplier2 = new FakeSupplier("Supplier B", "456 Elm St");
        suppliers.add(fakeSupplier1);
        suppliers.add(fakeSupplier2);
    }

    public static ArrayList<FakeSupplier> getAllSuppliers() {
        return suppliers;
    }

    public static FakeSupplier getSupplierById(int id) {
        if (id >= 0 && id < suppliers.size()) {
            return suppliers.get(id);
        }
        return null;
    }

    public static void addSupplier(FakeSupplier s) {
        suppliers.add(s);
    }

    public static void deleteSupplier(FakeSupplier s) {
        suppliers.remove(s);
    }
}
