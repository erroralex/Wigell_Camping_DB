package com.nilsson.ui.dialogs;

import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Gear;
import com.nilsson.registries.Inventory;
import com.nilsson.ui.UIUtil;
import javafx.collections.FXCollections;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

public class ShowAllGearDialog extends Dialog<Gear> {

    public ShowAllGearDialog() {

        // Dialog setup
        setTitle(LanguageManager.getInstance().getString("txt.showAllGearTitle"));
        setHeaderText(LanguageManager.getInstance().getString("txt.showAllGearHeader"));

        this.setOnShowing(dialogEvent -> {
            UIUtil.applyDialogSetup(this);
            getDialogPane().getStyleClass().add("show-all-dialog");
        });

        getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);

        // ListView for presentation
        VBox content = new VBox(10);
        content.setPrefSize(700, 800);

        List<Gear> gearList = Inventory.getInstance().getGearList();

        if (gearList == null || gearList.isEmpty()) {
            // No gear.
            content.getChildren().add(new javafx.scene.control.Label(
                    LanguageManager.getInstance().getString("txt.noGearFound")));
        } else {
            // Gear exists
            ListView<Gear> gearListView = new ListView<>(FXCollections.observableArrayList(gearList));

            // Add custom style class
            gearListView.getStyleClass().add("show-all-view");

            // Allow the list view to grow to fill the VBox
            VBox.setVgrow(gearListView, javafx.scene.layout.Priority.ALWAYS);

            content.getChildren().add(gearListView);
        }

        getDialogPane().setContent(content);
        setResizable(true);
    }
}