package DAO;

import Models.Item;
import Models.ItemNotFoundException;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemsDAO {
    public static final File INVENTORY_FILE = new File("src/main/resources/com/example/gui/Inventory.dat");

    public static ArrayList<Item> getAllItems(){
        ArrayList<Item> items = new ArrayList<>();
        if (!INVENTORY_FILE.exists() || INVENTORY_FILE.length() == 0) {
            System.out.println("Does not exist");
        }

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(INVENTORY_FILE)))
        {
            items = (ArrayList<Item>)input.readObject();
            for (Item i: items)
                System.out.println(i.getItemName()  + "  " + i.getItemCategory() + " " + i.getItemCategory().getSector().toString());

        } catch (EOFException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException a){
            System.out.println(a.getMessage());
        }
        return items;
    }

    public static void deleteItem(Item item) {
        ArrayList<Item> items = getAllItems();
        for(int i=0; i<items.size(); i++)
        {
            if(item.getItemName().equals(items.get(i).getItemName()))
                items.remove(i);
        }
        try (FileOutputStream employeeFile = new FileOutputStream(INVENTORY_FILE);
             ObjectOutputStream output = new ObjectOutputStream(employeeFile)) {
            output.writeObject(items);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Done");
    }

    public static Map<String, Integer> getItemsPurchasedStatistics(LocalDate start, LocalDate end) {
        ArrayList<Item> items = getAllItems();
        Map<String, Integer> purchasedStats = new HashMap<>();

        if (items != null) {
            for (Item item : items) {
                if((item.getPurchasedDate().isAfter(start) || item.getPurchasedDate().isEqual(start)) && (item.getPurchasedDate().isBefore(end) || item.getPurchasedDate().isEqual(end)))
                {
                    String itemName = item.getItemName(); // Use existing method in Item class
                    int quantity = item.getQuantity(); // Use existing method in Item class
                    purchasedStats.put(itemName, purchasedStats.getOrDefault(itemName, 0) + quantity);
                }
            }
        }
        return purchasedStats;
    }

    public static void addItemToFile(Item item)
    {
        ArrayList<Item> items = getAllItems();
        items.add(item);
        try (FileOutputStream employeeFile = new FileOutputStream(INVENTORY_FILE, false);
             ObjectOutputStream output = new ObjectOutputStream(employeeFile)){
            output.writeObject(items);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Item searchItem(String n) throws ItemNotFoundException {
        System.out.println("Search Item called");
        ArrayList<Item> items = ItemsDAO.getAllItems();
        for (Item i : items) {
            if (i.getItemName().equals(n)) {
                System.out.println("Search Successfully");
                return i;
            }
        }
        throw new ItemNotFoundException("There is no item with name: " + n);
    }

}
