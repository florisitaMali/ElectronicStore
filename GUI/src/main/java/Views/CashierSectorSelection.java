package Views;

import Models.Sector;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CashierSectorSelection {
    private final Dialog<Sector> sectorDialog = new Dialog<>();
    private final VBox vbox = new VBox(10);
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private RadioButton radioButton;
    private final ButtonType saveBtn = new ButtonType("Save");

    public Dialog<Sector> getSectorDialog() { return sectorDialog; }
    public VBox getVbox() { return vbox; }
    public ToggleGroup getToggleGroup() { return toggleGroup; }
    public RadioButton getRadioButton() { return radioButton; }
    public ButtonType getSaveBtn() { return saveBtn; }

    public Dialog<Sector> createSectorDialog(String title) {
        sectorDialog.setTitle(title);

        vbox.getChildren().add(new Label("Select sector for Cashier:"));

        for (Sector s : Sector.values()) {
            radioButton = new RadioButton(s.name());
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setUserData(s);
            vbox.getChildren().add(radioButton);
        }

        sectorDialog.getDialogPane().setContent(vbox);
        sectorDialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        sectorDialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtn) {
                return (Sector) toggleGroup.getSelectedToggle().getUserData();
            }
            return null;
        });

        return sectorDialog;
    }
}
