package DAO;

import Models.Category;
import Models.Item;
import Models.Sector;

import java.io.*;
import java.util.ArrayList;

public class CategoryDAO {
    public static final File CATEGORY_FILE = new File("src/main/resources/com/example/gui/Categories.dat");

    public static ArrayList<Category> getCategories()
    {
        ArrayList<Category> categories = new ArrayList<>();

        if (!CATEGORY_FILE.exists() || CATEGORY_FILE.length() == 0) {
            System.out.println("This file does not exist, or it does not have any category.");
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(CATEGORY_FILE))) {
                addCategory(new Category("Laptop", Sector.COMPUTERS));
                addCategory(new Category("TV", Sector.HOME_ENTERTAINMENT));
                addCategory(new Category("Camera", Sector.CAMERA));
                addCategory(new Category("Mobile Device", Sector.MOBILE_DEVICES));
                addCategory(new Category("PlayStation", Sector.HOME_ENTERTAINMENT));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        try (FileInputStream employeeFile = new FileInputStream(CATEGORY_FILE);
             ObjectInputStream input = new ObjectInputStream(employeeFile)){
            categories = (ArrayList<Category>) input.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return categories;
    }

    public static void addCategory(Category c) {
        ArrayList<Category> categories = getCategories();
        categories.add(c);
        try (FileOutputStream employeeFile = new FileOutputStream(CATEGORY_FILE, false);
             ObjectOutputStream input = new ObjectOutputStream(employeeFile)){
            input.writeObject(categories);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteCategory(Category c) {
        ArrayList<Category> categories = getCategories();
        System.out.println(categories.contains(c));
        categories.remove(c);
        try (FileOutputStream employeeFile = new FileOutputStream(CATEGORY_FILE, false);
             ObjectOutputStream input = new ObjectOutputStream(employeeFile)){
            input.writeObject(categories);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<Item> getSectorsItems(Sector se)
    {
        ArrayList<Category> categories = CategoryDAO.getCategories();
        ArrayList<Item> temp = new ArrayList<>();

        for(Category c: categories)
        {
            if(c.getSector().equals(se))
                temp.addAll(c.getItemsInThisCategory());
        }

        return temp;
    }

    public static ArrayList<Category> getSectorCategory(ArrayList<Sector> sectors)
    {
        ArrayList<Category> categories = CategoryDAO.getCategories();
        ArrayList<Category> temp = new ArrayList<>();

        for(Sector s: sectors) {
            for (Category c : categories) {
                if (c.getSector().equals(s))
                    temp.add(c);
            }
        }
        System.out.println("hereeee");
        return temp;
    }

    public static Category searchCategory(String s)
    {
        ArrayList<Category> categories = CategoryDAO.getCategories();
        for(Category c: categories)
        {
            if(c.getName().equals(s))
            {
                return c;
            }
        }
        return null;
    }
}
