package Views;

import DAO.CategoryDAO;
import DAO.SuppliersDAO;
import Models.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemDialogBox {

    public static Dialog<Item> createItemDialog(String title, Employee emp) {
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField itemNameField = new TextField();
        itemNameField.setPromptText("Item Name");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        VBox categoryBox = new VBox(5);
        categoryBox.setPadding(new Insets(5));
        categoryBox.setStyle("-fx-border-color: lightgrey; -fx-border-width: 1; -fx-padding: 5;");

        ToggleGroup categoryToggleGroup = new ToggleGroup();
        List<Category> categories = new ArrayList<>();

        if(emp instanceof Manager) {
            categories = CategoryDAO.getSectorCategory(((Manager)emp).getSectors());
        } else if(emp instanceof Cashier) {
            List<Sector> sector = new ArrayList<>();
            sector.add(((Cashier)emp).getSector());
            categories = CategoryDAO.getSectorCategory(sector);
        } else {
            categories = CategoryDAO.getSectorCategory(new ArrayList<>(Arrays.asList(Sector.values())));
        }

        for (Category c : categories) {
            RadioButton radioButton = new RadioButton(c.toString());
            radioButton.setToggleGroup(categoryToggleGroup);
            radioButton.setUserData(c);
            categoryBox.getChildren().add(radioButton);
        }

        ArrayList<Supplier> suppliers = SuppliersDAO.getAllSuppliers();
        ComboBox<Supplier> supplierComboBox = new ComboBox<>();
        supplierComboBox.getItems().addAll(suppliers);

        TextField purchasedPriceField = new TextField();
        purchasedPriceField.setPromptText("Purchased Price");

        TextField sellingPriceField = new TextField();
        sellingPriceField.setPromptText("Selling Price");

        TextField stockLimitField = new TextField();
        stockLimitField.setPromptText("Stock Limit");

        grid.add(new Label("Item Name:"), 0, 0);
        grid.add(itemNameField, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);
        grid.add(quantityField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryBox, 1, 2);
        grid.add(new Label("Supplier:"), 0, 3);
        grid.add(supplierComboBox, 1, 3);
        grid.add(new Label("Purchased Price:"), 0, 4);
        grid.add(purchasedPriceField, 1, 4);
        grid.add(new Label("Selling Price:"), 0, 5);
        grid.add(sellingPriceField, 1, 5);
        grid.add(new Label("Stock Limit:"), 0, 6);
        grid.add(stockLimitField, 1, 6);

        itemNameField.setId("itemNameField");
        quantityField.setId("quantityField");
        for(var node : categoryBox.getChildren()) {
            if (node instanceof RadioButton rb) {
                rb.setId("categoryRadioButton_" + rb.getUserData().toString());
            }
        }

        supplierComboBox.setId("supplierComboBox");
        purchasedPriceField.setId("purchasedPriceField");
        sellingPriceField.setId("sellingPriceField");
        stockLimitField.setId("stockLimitField");

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = itemNameField.getText().trim();
                if (name.isEmpty() || name.isBlank() || name.length() == 0) {
                    ShowAlert.showAlert("Invalid Input", "Item name cannot be empty.");
                    return null;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity < 0) {
                        ShowAlert.showAlert("Invalid Input", "Quantity cannot be negative.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    ShowAlert.showAlert("Invalid Input", "Quantity must be a valid number.");
                    return null;
                }

                RadioButton selectedRadioButton = (RadioButton) categoryToggleGroup.getSelectedToggle();
                if (selectedRadioButton == null) {
                    ShowAlert.showAlert("Invalid Input", "Please select a category.");
                    return null;
                }
                Category selectedCategory = (Category) selectedRadioButton.getUserData();

                Supplier supplier = supplierComboBox.getValue();
                if (supplier == null) {
                    ShowAlert.showAlert("Invalid Input", "Please select a supplier.");
                    return null;
                }

                double purchasedPrice;
                try {
                    purchasedPrice = Double.parseDouble(purchasedPriceField.getText().trim());
                    if (purchasedPrice < 0) {
                        ShowAlert.showAlert("Invalid Input", "Purchased price cannot be negative.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    ShowAlert.showAlert("Invalid Input", "Purchased price must be a valid number.");
                    return null;
                }

                double sellingPrice;
                try {
                    sellingPrice = Double.parseDouble(sellingPriceField.getText().trim());
                    if (sellingPrice < 0) {
                        ShowAlert.showAlert("Invalid Input", "Selling price cannot be negative.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    ShowAlert.showAlert("Invalid Input", "Selling price must be a valid number.");
                    return null;
                }

                long stockLimit;
                try {
                    stockLimit = Long.parseLong(stockLimitField.getText().trim());
                    if (stockLimit < 0) {
                        ShowAlert.showAlert("Invalid Input", "Stock limit cannot be negative.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    ShowAlert.showAlert("Invalid Input", "Stock limit must be a valid number.");
                    return null;
                }

                return new Item(name, quantity, selectedCategory, supplier, purchasedPrice, sellingPrice, stockLimit);
            }
            return null;
        });

        return dialog;
    }

}
