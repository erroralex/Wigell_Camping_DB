package com.nilsson.ui.dialogs;

import com.nilsson.ui.views.InventoryItemViewModel;
import com.nilsson.util.LanguageManager;
import com.nilsson.ui.UIUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.math.BigDecimal;

public class AddGearDialog extends Dialog<InventoryItemViewModel> {

    private final TextField modelField = new TextField();
    private final TextField capacityField = new TextField();
    private final TextField priceField = new TextField();
    private final ComboBox<String> typeBox = new ComboBox<>();

    private static final String TENT = "Tent";
    private static final String GEAR = "Gear";

    public AddGearDialog() {
        setTitle(LanguageManager.getInstance().getString("txt.addGearTitle"));
        setHeaderText(LanguageManager.getInstance().getString("txt.addGearHeader"));

        // Apply theme and mouse-drag
        this.setOnShowing(dialogEvent -> {
            UIUtil.applyDialogSetup(this);
        });

        ButtonType addButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.add"),
                ButtonBar.ButtonData.OK_DONE);

        ButtonType cancelButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.cancel"),
                ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(addButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPadding(new Insets(20,20,10,10));

        typeBox.getItems().addAll(TENT, GEAR);
        typeBox.setValue(TENT);

        grid.add(new Label(LanguageManager.getInstance().getString("txt.modelName")), 0, 0);
        grid.add(modelField, 1, 0);

        grid.add(new Label(LanguageManager.getInstance().getString("table.type")), 0, 1);
        grid.add(typeBox, 1, 1);

        grid.add(new Label(LanguageManager.getInstance().getString("table.capacity")), 0, 2);
        grid.add(capacityField, 1, 2);

        grid.add(new Label(LanguageManager.getInstance().getString("table.dailyPrice")), 0, 3);
        grid.add(priceField, 1, 3);

        getDialogPane().setContent(grid);
        Platform.runLater(modelField::requestFocus);

        Button addButton = (Button) getDialogPane().lookupButton(addButtonType);
        addButton.disableProperty().bind(modelField.textProperty().isEmpty().or(priceField.textProperty().isEmpty()));

        setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String selectedType = typeBox.getValue();
                    boolean isTent = TENT.equals(selectedType);

                    // Return a View Model
                    return new InventoryItemViewModel(
                            0L,
                            modelField.getText().trim(),
                            selectedType,
                            capacityField.getText().trim(),
                            new BigDecimal(priceField.getText().trim()),
                            false,
                            isTent,
                            null
                    );
                } catch (Exception e) { return null; }
            }
            return null;
        });
    }
}