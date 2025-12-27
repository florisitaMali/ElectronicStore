package Controller;

import Models.Bill;
import Models.Role;
import Views.PrintPane;
import Views.ReadBillsView;

public class ReadBillController {
    ReadBillsView view;
    PrintPane printPane;

    public ReadBillController(ReadBillsView view){
        this.view = view;
        printPane = new PrintPane();
        enableButtons();
    }

    private void enableButtons() {
        printBillAction();
        setOkPrintedButton();
    }

    private void printBillAction() {
        view.getReadBillTableView().getBillTable().setOnMouseClicked(
                event ->   {
                    if(event.getClickCount() == 2)
                    {
                        Bill selectedBill = view.getReadBillTableView().getBillTable().getSelectionModel().getSelectedItem();
                        if(selectedBill != null)
                        {
                            printPane.setBill(selectedBill);
                            view.setCenter(printPane.getPrintedBill());
                        }
                    }
                }
        );
    }

    private void setOkPrintedButton() {
        printPane.getOkBtn().setOnAction(e -> {
            view.setCenter(view.getReadBillTableView().getBillTableChild());
        });
    }
}
