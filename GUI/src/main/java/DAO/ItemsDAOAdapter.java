package DAO;

import Models.Item;
import Models.ItemNotAvailableException;
import Models.ItemNotFoundException;

import java.util.ArrayList;

// Wraps static ItemsDAO
public class ItemsDAOAdapter implements ItemsRepository {
    @Override
    public ArrayList<Item> getAllItems() {
        return ItemsDAO.getAllItems();
    }

    @Override
    public Item searchItem(String itemName) throws ItemNotFoundException {
        return ItemsDAO.searchItem(itemName);
    }
}