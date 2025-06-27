package DAO;

import Models.Supplier;

import java.io.*;
import java.util.ArrayList;

public class SuppliersDAO {
    public static final File SUPPLIERS_FILE = new File("src/main/resources/com/example/gui/Suppliers.dat");

    public static ArrayList<Supplier> getAllSuppliers() {
        ArrayList<Supplier> suppliers = new ArrayList<>();
        if (!SUPPLIERS_FILE.exists() || SUPPLIERS_FILE.length() == 0) {
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(SUPPLIERS_FILE))) {
                Supplier supplierA = new Supplier("Supplier A", "Rruga e Durrësit - Tirana");
                Supplier supplierB = new Supplier("Supplier B", "Rruga e Myslym Shyri - Tirana");
                Supplier supplierC = new Supplier("Supplier C", "Rruga 28 Nëntori - Vlora");
                Supplier supplierD = new Supplier("Supplier D", "Rruga Justin Godard - Shkodër");
                Supplier supplierE = new Supplier("Supplier E", "Rruga Skënderbej - Korça");
                addSupplier(supplierA);
                addSupplier(supplierB);
                addSupplier(supplierC);
                addSupplier(supplierD);
                addSupplier(supplierE);
                System.out.println("Done");
            } catch (EOFException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());;
            }
        }

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(SUPPLIERS_FILE))) {
            suppliers = (ArrayList<Supplier>) input.readObject();
        } catch (EOFException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return suppliers;
    }

    public static void addSupplier(Supplier s)
    {
        ArrayList<Supplier> suppliers = getAllSuppliers();
        suppliers.add(s);
        try (FileOutputStream employeeFile = new FileOutputStream(SUPPLIERS_FILE, false);
             ObjectOutputStream input = new ObjectOutputStream(employeeFile)){
            input.writeObject(suppliers);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteSupplier(Supplier supplier) {
        ArrayList<Supplier> suppliers = getAllSuppliers();
        for(int i=0; i<suppliers.size(); i++)
        {
            if(supplier.getSupplierName().toLowerCase().equals(suppliers.get(i).getSupplierName().toLowerCase()))
                suppliers.remove(i);
        }
        try (FileOutputStream employeeFile = new FileOutputStream(SUPPLIERS_FILE);
             ObjectOutputStream output = new ObjectOutputStream(employeeFile)) {
            output.writeObject(suppliers);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Done");
    }
}
