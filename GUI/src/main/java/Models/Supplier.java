package Models;

import DAO.ItemsDAO;
import DAO.ItemsDAOAdapter;
import DAO.ItemsRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Supplier implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static ItemsRepository itemsDAO;

    private String supplierName;
    private String address;
    private transient StringProperty supplierNameProperty;
    private transient StringProperty addressProperty;
    private ArrayList<Item> products;

    public Supplier(String supplierName, String address) {
        this.supplierName = supplierName;
        this.address = address;
        this.products = new ArrayList<>();
        this.supplierNameProperty = new SimpleStringProperty(supplierName);
        this.addressProperty = new SimpleStringProperty(address);
        itemsDAO = new ItemsDAOAdapter();
    }

    public Supplier(String supplierName) {
        this(supplierName, "Unknown Address");
    }

    // Getters
    public StringProperty supplierNameProperty() {
        if (supplierNameProperty == null) {
            supplierNameProperty = new SimpleStringProperty(this, "supplierName", supplierName);
        }
        return supplierNameProperty;
    }

    public String getSupplierName() {
        return supplierNameProperty().get();
    }

    public StringProperty addressProperty() {
        if (addressProperty == null) {
            addressProperty = new SimpleStringProperty(this, "address", address);
        }
        return addressProperty;
    }

    public String getAddress() {
        return addressProperty().get();
    }

    // Setters
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
        if (supplierNameProperty != null) {
            supplierNameProperty.set(supplierName);
        }
    }

    public void setAddress(String address) {
        this.address = address;
        if (addressProperty != null) {
            addressProperty.set(address);
        }
    }

    public ArrayList<Item> getProducts() {
        ArrayList<Item> items = itemsDAO.getAllItems();
        ArrayList<Item> temp = new ArrayList<>();
        for (Item item : items) {
            if (item.getItemSupplier().getSupplierName().equalsIgnoreCase(getSupplierName())) {
                temp.add(item);
            }
        }
        return temp;
    }

    public static void setItemsDAO(ItemsRepository dao) {
        itemsDAO = dao;
    }

    @Override
    public String toString() {
        return getSupplierName();
    }
}
