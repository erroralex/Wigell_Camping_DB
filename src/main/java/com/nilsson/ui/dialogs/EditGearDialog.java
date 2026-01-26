package com.nilsson.ui.dialogs;

import com.nilsson.ui.views.InventoryItemViewModel;
import com.nilsson.util.LanguageManager;
import com.nilsson.ui.UIUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.math.BigDecimal;

public class EditGearDialog extends Dialog<InventoryItemViewModel> {

    private final TextField modelField = new TextField();
    private final TextField capacityField = new TextField();
    private final TextField priceField = new TextField();
    private final ComboBox<String> typeBox = new ComboBox<>();
    private final InventoryItemViewModel item;

    public EditGearDialog(InventoryItemViewModel item) {
        this.item = item;
        setTitle("Edit Item");
        setHeaderText("Edit details for " + item.getModel());

        // Apply theme and mouse-drag
        this.setOnShowing(dialogEvent -> {
            UIUtil.applyDialogSetup(this);
        });

        ButtonType saveButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.save"),
                ButtonBar.ButtonData.OK_DONE);

        ButtonType cancelButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.cancel"),
                ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPadding(new Insets(20,20,10,10));

        modelField.setText(item.getModel());
        capacityField.setText(item.getCapacity());
        priceField.setText(String.valueOf(item.getCost()));

        typeBox.getItems().addAll("Tent", "Backpack", "Other");
        typeBox.setValue(item.getType());

        if (item.isTentEntity()) {
            typeBox.setDisable(true);
        }

        grid.add(new Label("Model:"), 0, 0);
        grid.add(modelField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeBox, 1, 1);
        grid.add(new Label("Capacity:"), 0, 2);
        grid.add(capacityField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);

        getDialogPane().setContent(grid);
        Platform.runLater(modelField::requestFocus);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Update the view model
                    item.setModel(modelField.getText().trim());
                    item.setType(typeBox.getValue());
                    item.setCapacity(capacityField.getText().trim());
                    item.setCost(new BigDecimal(priceField.getText().trim()));
                    return item;
                } catch (Exception e) { return null; }
            }
            return null;
        });
    }
}