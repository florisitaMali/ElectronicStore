package Views;

import DAO.CategoryDAO;
import Models.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class CategoryDialogBox {
    private static ButtonType saveButtonType = new ButtonType("Save");
    private static TextField categoryNameField = new TextField();
    private static ComboBox<Sector> sectorsComboBox = new ComboBox<>();

    public static ButtonType getSaveButtonType() {
        return saveButtonType;
    }
    public static TextField getCategoryNameField() {
        return categoryNameField;
    }
    public static ComboBox<Sector> getSectorsComboBox() {
        return sectorsComboBox;
    }

    public static Dialog<Category> createItemDialog(String title, Manager manager) {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle(title);

        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        categoryNameField.setPromptText("Category Name");

        sectorsComboBox.getItems().addAll(manager.getSectors());

        grid.add(new Label("Category Name:"), 0, 0);
        grid.add(categoryNameField, 1, 0);
        grid.add(new Label("Sector:"), 0, 1);
        grid.add(sectorsComboBox, 1, 1);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Category c = new Category(categoryNameField.getText(), sectorsComboBox.getValue());
                CategoryDAO.addCategory(c);
                return c;
            }
            return null;
        });

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
        return dialog;
    }
}

