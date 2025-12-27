package Views;

import DAO.BillDAO;
import Models.Administrator;
import Models.Bill;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ReadBillTableView {
    private final ReadBillsView readBillsView;

    private final TableView<Bill> billTable = new TableView<>();
    private final TableColumn<Bill, Long> billNumCol = new TableColumn<>("BillNumber");
    private final TableColumn<Bill, String> nameCol = new TableColumn<>("Bill");
    private final TableColumn<Bill, String> dateCol = new TableColumn<>("Date");
    private final TableColumn<Bill, Double> totalPriceCol = new TableColumn<>("Total Price");
    private final TextField totalIncome = new TextField();
    private ObservableList<Bill> tableBillList;

    public ReadBillTableView(ReadBillsView view) {
        readBillsView = view;

        if(view.getCurrentUser() instanceof Administrator)
            tableBillList = FXCollections.observableArrayList(BillDAO.getAllBills());
        else
            tableBillList = FXCollections.observableArrayList(BillDAO.getDayBills(readBillsView.getCurrentUser()));

        billTable.setItems(tableBillList);
        setUpColumns();
    }

    public TableView<Bill> getBillTable() { return billTable; }
    public TableColumn<Bill, Long> getBillNumCol() { return billNumCol; }
    public TableColumn<Bill, String> getNameCol() { return nameCol; }
    public TableColumn<Bill, String> getDateCol() { return dateCol; }
    public TableColumn<Bill, Double> getTotalPriceCol() { return totalPriceCol; }
    public ObservableList<Bill> getTableBillList() { return tableBillList; }

    private void setUpColumns() {
        // Set the cell value factories for the columns
        billNumCol.setCellValueFactory(new PropertyValueFactory<>("billNumber"));
        billNumCol.setSortable(true);
        billNumCol.setPrefWidth(200);

        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                "Bill" + cellData.getValue().getBillNumber() + cellData.getValue().getSaleDate().getDayOfMonth() + cellData.getValue().getSaleDate().getMonth() + cellData.getValue().getSaleDate().getYear() + ".txt"));
        nameCol.setSortable(true);
        nameCol.setPrefWidth(200);

        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                " " + cellData.getValue().getSaleDate().getDayOfMonth() + " "  + cellData.getValue().getSaleDate().getMonth() +" " + cellData.getValue().getSaleDate().getYear()));
        dateCol.setSortable(true);
        dateCol.setPrefWidth(200);

        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceCol.setSortable(true);
        totalPriceCol.setPrefWidth(200);

        billTable.setMaxSize(800, 600);

        billTable.getColumns().addAll(billNumCol, nameCol, dateCol, totalPriceCol);
    }

    private TextField getTotalIncome() {
        double total = 0;
        for (Bill b : tableBillList)
            total += b.getTotalPrice();

        totalIncome.setText(String.valueOf(total));
        totalIncome.setAlignment(Pos.CENTER_RIGHT);
        totalIncome.setMaxWidth(800);
        return totalIncome;
    }

    public VBox getBillTableChild() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.getChildren().addAll(billTable, getTotalIncome());
        pane.setAlignment(Pos.CENTER);
        return pane;
    }

    private String getMonth(int i)
    {
        switch (i)
        {
            case 0: return "Jan";
            case 1: return "Feb";
            case 2: return "Mar";
            case 3: return "Apr";
            case 4: return "May";
            case 5: return "Jun";
            case 6: return "Jul";
            case 7: return "Aug";
            case 8: return "Sep";
            case 9: return "Oct";
            case 10: return "Nov";
            case 11: return "Dec";
        }
        return null;
    }
}
