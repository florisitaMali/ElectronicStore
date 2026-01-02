package DAO;

import Models.Item;
import Models.ItemNotAvailableException;
import Models.ItemNotFoundException;

import java.util.ArrayList;

public interface ItemsRepository {
    ArrayList<Item> getAllItems();
    Item searchItem(String itemName)  throws ItemNotFoundException;
}
