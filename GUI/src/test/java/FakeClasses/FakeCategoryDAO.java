package FakeClasses;

import DAO.CategoryDAO;
import Models.Category;
import Models.Item;
import Models.Sector;

import java.util.ArrayList;
import java.util.List;

/**
 * Fake CategoryDAO for unit testing.
 * No database access – uses in-memory lists.
 */
public class FakeCategoryDAO extends CategoryDAO {

    private static List<Category> categories = new ArrayList<>();
    private static List<Item> items = new ArrayList<>();

    /* ===================== SETUP METHODS ===================== */

    public static void setCategories(List<Category> fakeCategories) {
        categories = fakeCategories;
    }

    public static void setItems(List<Item> fakeItems) {
        items = fakeItems;
    }

    public static void reset() {
        categories.clear();
        items.clear();
    }

    /* ===================== OVERRIDDEN METHODS ===================== */

    public static ArrayList<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public static List<Category> getSectorCategory(List<Sector> sectors) {
        List<Category> result = new ArrayList<>();

        for (Category c : categories) {
            if (sectors.contains(c.getSector())) {
                result.add(c);
            }
        }
        return result;
    }

    public static List<Item> getSectorsItems(Sector sector) {
        List<Item> result = new ArrayList<>();

        for (Item i : items) {
            if (i.getSector() == sector) {
                result.add(i);
            }
        }
        return result;
    }

    /* ===================== OPTIONAL (NO-OP) ===================== */

    public static void addCategory(Category c) {
        categories.add(c);
    }

    public static void deleteCategory(Category c) {
        categories.remove(c);
    }
}
