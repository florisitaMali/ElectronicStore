package Controller;

import DAO.ItemsDAO;
import DAO.SuppliersDAO;
import Models.Item;
import Models.Supplier;
import Views.ItemDialogBox;
import Views.ManageInventoryView;
import Views.ShowAlert;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class ManageInventoryController {
    private final ManageInventoryView view;

    public ManageInventoryController(ManageInventoryView view)
    {
        this.view = view;
        enableButton();
    }

    public void enableButton()
    {
        setAddButtonAction();
        setEditButtonAction();
        setDeleteButtonAction();
        setAddSupplierButtonAction();
        setSearchButtonAction();
    }

    private void setAddButtonAction()
    {
        view.getAddButton().setOnAction(e -> addItem());
    }

    private void setEditButtonAction(){
        view.getEditButton().setOnAction(e -> editItem());
    }

    private void setDeleteButtonAction(){
        view.getDeleteButton().setOnAction(e -> deleteItem());
    }

    private void setSearchButtonAction()
    {
        view.getSearchButton().setOnAction(e -> searchItem(view.getSearchField().getText()));
    }

    private void searchItem(String itemName) {
        List<Item> filteredList = view.getItemList().stream().
                filter(item -> item.getItemName().toLowerCase().contains(itemName.toLowerCase())).
                collect(Collectors.toList());//Collect all the items which satisfied the condition
        view.getInventoryTable().getItems().setAll(filteredList);
    }

    private void addItem() {
        Dialog<Item> dialog = ItemDialogBox.createItemDialog("Add New Item", view.getCurrentUser());
        dialog.showAndWait().ifPresent(item -> {
            if (item.getQuantity() < 0 || item.getPurchasedPrice() < 0 || item.getSellingPrice() < 0 || item.getStockLimit() < 0) {
                ShowAlert.showAlert("Invalid Input", "Numeric values cannot be negative.");
                return;
            }
            view.getItemList().add(item);
            ItemsDAO.addItem(item);
            view.getInventoryTable().getItems().add(item);
            view.getInventoryTable().refresh();
        });
    }


    private void editItem() {
        System.out.println("Edit button clicked");

        Item selectedItem = view.getInventoryTable()
                .getSelectionModel()
                .getSelectedItem();

        if (selectedItem == null) {
            ShowAlert.showAlert("No Selection", "Please select an item to edit.");
            return;
        }

        System.out.println("Selected Item: " + selectedItem.getItemName());

        Dialog<Item> dialog =
                ItemDialogBox.createItemDialog("Edit Item", view.getCurrentUser());

        GridPane grid = (GridPane) dialog.getDialogPane().getContent();

        // ===== ACCESS FIELDS BY INDEX (MATCHING DIALOG ORDER) =====
        TextField nameField = (TextField) grid.getChildren().get(1);
        TextField quantityField = (TextField) grid.getChildren().get(3);
        VBox categoryBox = (VBox) grid.getChildren().get(5);
        ComboBox<Supplier> supplierComboBox = (ComboBox<Supplier>) grid.getChildren().get(7);
        TextField purchasedPriceField = (TextField) grid.getChildren().get(9);
        TextField sellingPriceField = (TextField) grid.getChildren().get(11);
        TextField stockLimitField = (TextField) grid.getChildren().get(13);

        // ===== PREFILL VALUES =====
        nameField.setText(selectedItem.getItemName());
        quantityField.setText(String.valueOf(selectedItem.getQuantity()));
        purchasedPriceField.setText(String.valueOf(selectedItem.getPurchasedPrice()));
        sellingPriceField.setText(String.valueOf(selectedItem.getSellingPrice()));
        stockLimitField.setText(String.valueOf(selectedItem.getStockLimit()));

        supplierComboBox.setValue(selectedItem.getItemSupplier());

        // ===== SELECT CATEGORY RADIO BUTTON =====
        for (var node : categoryBox.getChildren()) {
            if (node instanceof RadioButton rb) {
                if (rb.getUserData().equals(selectedItem.getItemCategory())) {
                    rb.setSelected(true);
                    break;
                }
            }
        }

        // ===== HANDLE SAVE =====
        dialog.showAndWait().ifPresent(updatedItem -> {
            if (updatedItem != null) {
                updatedItem.setId(selectedItem.getId()); // keep the original id

                // Preserve unchanged fields
                if (updatedItem.getItemName().isEmpty()) updatedItem.setItemName(selectedItem.getItemName());
                if (updatedItem.getQuantity() == 0) updatedItem.setQuantity(selectedItem.getQuantity());
                if (updatedItem.getItemCategory() == null) updatedItem.setItemCategory(selectedItem.getItemCategory());
                if (updatedItem.getItemSupplier() == null) updatedItem.setItemSupplier(selectedItem.getItemSupplier());
                if (updatedItem.getPurchasedPrice() == 0) updatedItem.setPurchasedPrice(selectedItem.getPurchasedPrice());
                if (updatedItem.getSellingPrice() == 0) updatedItem.setSellingPrice(selectedItem.getSellingPrice());
                if (updatedItem.getStockLimit() == 0) updatedItem.setStockLimit(selectedItem.getStockLimit());

                // Update in DB
                ItemsDAO.updateItem(updatedItem);

                // Update list and table
                view.getItemList().set(view.getItemList().indexOf(selectedItem), updatedItem);
                view.getInventoryTable().getItems().set(view.getInventoryTable().getItems().indexOf(selectedItem), updatedItem);
            }
        });


    }

    private void deleteItem() {
        Item selectedItem = view.getInventoryTable().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ShowAlert.showAlert("No Selection", "Please select an item to delete.");
            return;
        }
        ItemsDAO.softDeleteItem(selectedItem);
        view.getItemList().remove(selectedItem);
        view.getInventoryTable().getItems().remove(selectedItem);
    }

    private void setAddSupplierButtonAction(){
        view.getAddSupplierButton().setOnAction(e->
        {
            if(view.getNameField() != null) {
                SuppliersDAO.addSupplier(new Supplier(view.getNameField().getText()));
                view.getNameField().clear();
            }
            else
                ShowAlert.showAlert("Invalid data", "Please enter a name.");
        });
    }
}
