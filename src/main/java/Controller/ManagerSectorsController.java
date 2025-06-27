package Controller;

import DAO.CategoryDAO;
import DAO.ItemsDAO;
import DAO.SuppliersDAO;
import Models.*;
import Views.CategoryDialogBox;
import Views.ItemDialogBox;
import Views.ManagerSectorsView;
import Views.ShowAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;

public class ManagerSectorsController {
    private ManagerSectorsView view;

    public ManagerSectorsController(ManagerSectorsView view)
    {
        this.view = view;
        enableButtons();
    }

    private void enableButtons()
    {
        addSearchButtonAction();
        addAddButtonAction();
        addEditButtonAction();
        addDeleteButtonAction();
        addAddCategoryAction();
        addSectorComboBoxAction();
        addDeleteCategoryButtonAction();
        addSupplierAction();
    }

    private void addSearchButtonAction(){
        view.getSearchButton().setOnAction(e->
                searchItem(view.getSearchField().getText())
        );
    }

    private void addAddButtonAction(){
        view.getAddButton().setOnAction(e->
                addItem()
        );
    }

    private void addEditButtonAction(){
        view.getEditButton().setOnAction(e->
                editItem()
        );
    }

    private void addDeleteButtonAction(){
        view.getDeleteButton().setOnAction(e->
                deleteItem()
        );
    }

    private void addAddCategoryAction(){
        view.getAddCategoryButton().setOnAction(e->
                addCategory()
        );
    }

    private void addSectorComboBoxAction()
    {
        view.getSectorComboBox().setOnAction(e-> {
            Sector sector = view.getSectorComboBox().getValue();
            view.loadData(sector);
        }   );
    }


    private void searchItem(String itemName) {
        view.getSearchField().textProperty().addListener((observable, oldValue, newValue) -> {
            filterItems(newValue);
        });
    }

    private void filterItems(String filter) {
        if (filter == null || filter.length() < 2) {
            view.getInventoryTable().setItems(FXCollections.observableArrayList(view.getItemList())); // Show all items if filter is less than 2 characters
        } else {
            ObservableList<Item> filteredItems = FXCollections.observableArrayList();
            for (Item item : view.getItemList()) {
                if (item.getItemName().toLowerCase().startsWith(filter.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
            view.getInventoryTable().setItems(filteredItems);
        }
    }

    private void addItem() {
        Dialog<Item> dialog = ItemDialogBox.createItemDialog("Add New Item", (Manager) view.getCurrentUser());
        dialog.showAndWait().ifPresent(item -> {
            view.getItemList().add(item);
            view.getInventoryTable().getItems().add(item);
            ItemsDAO.addItemToFile(item);
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
        ((TextField) grid.getChildren().get(1)).setText(selectedItem.getItemName()); // Item Name
        ((TextField) grid.getChildren().get(3)).setText(String.valueOf(selectedItem.getQuantity())); // Quantity

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

        view.getItemList().remove(selectedItem);
        ItemsDAO.deleteItem(selectedItem);
        view.getInventoryTable().getItems().remove(selectedItem);
    }

    private void addCategory() {
        view.getAddCategoryButton().setOnAction(event1 -> {
            CategoryDialogBox.createItemDialog("Add Category", (Manager) view.getCurrentUser());
        });
    }

    private void addDeleteCategoryButtonAction() {
        view.getDeleteCategoryButton().setOnAction(e -> {
            Dialog<Void> categoryDialog = new Dialog<>();
            categoryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            VBox vBox = new VBox(10);

            ArrayList<CheckBox> categoriesCheckBox = new ArrayList<>();
            ArrayList<Category> categories = CategoryDAO.getSectorCategory(((Manager) view.getCurrentUser()).getSectors());

            for (Category category : categories) {
                CheckBox checkBox = new CheckBox(category.getName());
                categoriesCheckBox.add(checkBox);
                vBox.getChildren().add(checkBox);
            }

            categoryDialog.getDialogPane().setContent(vBox);
            categoryDialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    ArrayList<Category> toDelete = new ArrayList<>();
                    for (int i = 0; i < categoriesCheckBox.size(); i++) {
                        if (categoriesCheckBox.get(i).isSelected()) {
                            toDelete.add(categories.get(i));
                        }
                    }

                    for (Category category : toDelete) {
                        System.out.println("Deleting category: " + category.getName());
                        CategoryDAO.deleteCategory(category);
                    }

                    categories.removeAll(toDelete);
                }
                return null;
            });

            categoryDialog.showAndWait();
        });

    }

    public void addSupplierAction()
    {
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
