package Views;

import Models.Sector;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ManagerSectorSelection {
    public final ArrayList<CheckBox> sectors = new ArrayList<>();

    public final ArrayList<CheckBox> getSectors() {
        return sectors;
    }

    public Dialog<ArrayList<Sector>> createSectorDialog(String title) {
        Dialog<ArrayList<Sector>> sectorDialog = new Dialog<>();
        sectorDialog.setTitle(title);

        VBox vbox = new VBox(10);

        for(Sector s: Sector.values())
        {
            sectors.add(new CheckBox(s.name()));
        }

        // Define button types
        ButtonType saveBtn = new ButtonType("Save");
        sectorDialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        vbox.getChildren().add(new Label("Select the sectors to be managed:"));
        vbox.getChildren().addAll(sectors);
        sectorDialog.getDialogPane().setContent(vbox);

        // Set the result converter for the dialog
        sectorDialog.setResultConverter(dialogButton -> {
            ArrayList<Sector> s = new ArrayList<>();
            if (dialogButton == saveBtn) {
                for(int i=0; i<sectors.size(); i++)
                {
                    if(sectors.get(i).isSelected())
                        s.add(Sector.values()[i]);
                }
                return s;
            }
            return null;
        });

        return sectorDialog;
    }
}
