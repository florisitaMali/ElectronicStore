package Models;

import DAO.CategoryDAO;
import DAO.ItemsDAO;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private String name;
    private Sector sector;

    public Category(String name, Sector se){
        this.name = name;
        this.sector = se;
    }
    public void setSector(Sector s){ this.sector = s;}
    public Sector getSector() { return sector;}

    public void setName(String name){ this.name = name;}
    public String getName(){ return name;}
    @Override
    public String toString(){ return name;}

    public ArrayList<Item> getItemsInThisCategory()
    {
        ArrayList<Item> items = ItemsDAO.getAllItems();
        ArrayList<Item> temp = new ArrayList<>();

        for(Item i: items)
        {
            if(i.getItemCategory().getName().equals(this.name)) {
                temp.add(i);
            }
        }
        return temp;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Category && ((Category)o).name.equals(this.name))
        {
            return true;
        }
        return false;
    }
}
