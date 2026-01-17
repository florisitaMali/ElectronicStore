package Controller;

import DAO.BillDAO;
import Models.*;
import Views.DeleteItemView;
import Views.GenerateBillView;
import Views.PrintPane;
import Views.ShowAlert;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class GenerateBillController {
    private final GenerateBillView view;
    private PrintPane printPane;

    public GenerateBillController(GenerateBillView view) {
        this.view = view;
        printPane = new PrintPane();
        enableButtons();
    }

    public PrintPane getPrintPane() {
        return printPane;
    }

    private void enableButtons() {
        setDeleteButtonAction();
        setAddButtonAction();
        setCancelButtonAction();
        setApproveButtonAction();
        setPrintButtonAction();
        setOkButtonAction();
        setSearchFieldAction();
    }

    private void setDeleteButtonAction() {
        view.getDeleteSoldItemBtn().setOnAction(e -> {
            view.getPrimaryPane().setCenter(view.getDeleteItemView().deleteSoldItem(view));
        });
    }

    private void setCancelButtonAction() {
        view.getDeleteItemView().getCancelBtn().setOnAction(e -> {
            VBox vBox = new VBox(view.getBillTable(), view.getTotalAmount());
            vBox.setAlignment(Pos.CENTER);
            view.getPrimaryPane().setCenter(vBox);
        });
    }

    private void setApproveButtonAction() {
        DeleteItemView deleteItemView = view.getDeleteItemView();
        if (deleteItemView != null) {
            deleteItemView.getApproveBtn().setOnAction(e -> {
                ArrayList<CheckBox> checkBoxes = deleteItemView.getSoldItemCheckBox();
                ObservableList<SoldItem> soldItems = view.getSoldItems();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        view.deleteSoldItem(soldItems.get(i));
                    }
                }

                // Update the checkboxes in the DeleteItemView
                view.getDeleteItemView().updateCheckBoxes(view.getSoldItems());
                view.updateTotalAmount();
                // Return to the main bill table view
                VBox vBox = new VBox(view.getBillTable(), view.getTotalAmount());
                vBox.setAlignment(Pos.CENTER);
                view.getPrimaryPane().setCenter(vBox);
            });
        } else {
            System.out.println("DeleteItemView is not initialized.");
        }
    }

    private void setAddButtonAction() {
        view.getAddSoldItemBtn().setOnAction(event -> {
            String itemName = view.getItemNameTF().getText();
            int quantity = Integer.parseInt(view.getItemQuantityTF().getText());

            // Create new item
            try {
                SoldItem newItem = new SoldItem(itemName, quantity);
                view.addSoldItems(newItem);
                view.updateTotalAmount();

                // Clear input fields
                view.getItemNameTF().clear();
                view.getItemQuantityTF().clear();
            } catch(ItemNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (ItemNotAvailableException a) {
                showAlert("Warning", a.getMessage());
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    private void setPrintButtonAction() {
        view.getPrintBillBtn().setOnAction(e -> {
            Bill bill = view.getBill();
            // Use the current bill from the table view
            printPane.setBill(bill);
            view.getPrimaryPane().setCenter(printPane.getPrintedBill());
            // Ensure the printPane is set with the latest bill
            view.getAddSoldItemBtn().setDisable(true);
            view.getDeleteSoldItemBtn().setDisable(true);
            view.getPrintBillBtn().setDisable(true);
            view.getItemQuantityTF().setDisable(true);
            view.getItemNameTF().setDisable(true);
            printCurrentBill(bill);
        });
    }

    private void setOkButtonAction() {
        Button okBtn = printPane.getOkBtn();
        okBtn.setOnAction(e -> {
            printPane.setBill(view.getBill());
            view.createNewBill();
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(view.getBillTable(), view.getTotalAmount());
            view.getPrimaryPane().setCenter(vBox);
            view.getAddSoldItemBtn().setDisable(false);
            view.getDeleteSoldItemBtn().setDisable(false);
            view.getPrintBillBtn().setDisable(false);
            view.getItemQuantityTF().setDisable(false);
            view.getItemNameTF().setDisable(false);
        });
    }

    private void printCurrentBill(Bill bill) {
        System.out.println();
        String fileName = "Bill" + bill.getBillNumber() + "_" + bill.getSaleDate().getDayOfMonth() + "_" + bill.getSaleDate().getMonth()+ "_" + bill.getSaleDate().getYear() + "_" + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(bill.printBill());
            System.out.println(bill.printBill());
            BillDAO.saveBill(bill);
            System.out.println("Done");
        } catch (IOException ex) {
            ShowAlert.showAlert("Error", "Error writing bill to file: " + ex.getMessage());
        }
    }

    private void setSearchFieldAction()
    {
        view.getItemNameTF().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                view.getItemList().setVisible(true);
                view.getItemList().setItems(view.getItems());
            } else {
                ObservableList<Item> filteredItems = view.getItems().filtered(item -> item.getItemName().toLowerCase().startsWith(newValue.toLowerCase()));
                view.getItemList().setItems(filteredItems);
                view.getItemList().setVisible(true);
            }
        });

        view.getItemList().setOnMouseClicked(event -> {
            //Double click to select the items
            //You can also write it before
            if (event.getClickCount() == 2) {
                Item selectedItem = view.getItemList().getSelectionModel().getSelectedItem();
                view.getItemNameTF().setText(selectedItem.getItemName());
                view.getItemList().setVisible(true);
                view.getItemList().setItems(view.getItems());
            }
        });
    }
}
