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
            view.getItemList().add(item);
            ItemsDAO.addItemToFile(item);
            view.getInventoryTable().getItems().add(item);
            view.getInventoryTable().refresh();
        });
    }

    private void editItem() {
        System.out.println("Edit button clicked");
        Item selectedItem = view.getInventoryTable().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ShowAlert.showAlert("No Selection", "Please select an item to edit.");
            return;
        }

        System.out.println("Selected Item: " + selectedItem.getItemName());

        Dialog<Item> dialog = ItemDialogBox.createItemDialog("Edit Item", view.getCurrentUser());

        // Pre-fill dialog fields with the selected item's values
        GridPane grid = (GridPane) dialog.getDialogPane().getContent();
        ((TextField) grid.getChildren().get(1)).setText(selectedItem.getItemName());
        ((TextField) grid.getChildren().get(3)).setText(String.valueOf(selectedItem.getQuantity()));

        VBox categoryBox = (VBox) grid.getChildren().get(5);
        categoryBox.getChildren().forEach(node -> {
            if (node instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) node;
                if (selectedItem.getItemCategory().toString().equals(radioButton.getText())) {
                    radioButton.setSelected(true);
                }
            } else {
                System.out.println("Node is not a RadioButton: " + node.getClass().getName());
            }
        });

        ComboBox<Supplier> supplierComboBox = (ComboBox<Supplier>) grid.getChildren().get(7);
        supplierComboBox.setValue(selectedItem.getItemSupplier());

        ((TextField) grid.getChildren().get(9)).setText(String.valueOf(selectedItem.getPurchasedPrice())); // Purchased Price
        ((TextField) grid.getChildren().get(11)).setText(String.valueOf(selectedItem.getSellingPrice())); // Selling Price

        dialog.showAndWait().ifPresent(updatedItem -> {
            if (updatedItem != null) {
                // Keep unchanged fields as they were
                if (updatedItem.getItemName().isEmpty()) {
                    updatedItem.setItemName(selectedItem.getItemName());
                }
                if (updatedItem.getQuantity() == 0) {
                    updatedItem.setQuantity(selectedItem.getQuantity());
                }
                if (updatedItem.getItemCategory() == null) {
                    updatedItem.setItemCategory(selectedItem.getItemCategory());
                }
                if (updatedItem.getItemSupplier() == null) {
                    updatedItem.setItemSupplier(selectedItem.getItemSupplier());
                }
                if (updatedItem.getPurchasedPrice() == 0) {
                    updatedItem.setPurchasedPrice(selectedItem.getPurchasedPrice());
                }
                if (updatedItem.getSellingPrice() == 0) {
                    updatedItem.setSellingPrice(selectedItem.getSellingPrice());
                }
                // Update the item in the list and the table
                view.getItemList().set(view.getItemList().indexOf(selectedItem), updatedItem);
                view.getInventoryTable().getItems().set(view.getInventoryTable().getItems().indexOf(selectedItem), updatedItem);

                ItemsDAO.deleteItem(selectedItem);
                ItemsDAO.addItemToFile(updatedItem);
            }
        });
    }

    private void deleteItem() {
        Item selectedItem = view.getInventoryTable().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ShowAlert.showAlert("No Selection", "Please select an item to delete.");
            return;
        }
        ItemsDAO.deleteItem(selectedItem);
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
