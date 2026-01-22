package com.nilsson.ui.dialogs;

import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Gear;
import com.nilsson.ui.UIUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class AddGearDialog extends Dialog<Gear> {

    private final TextField modelField = new TextField();
    private final TextField capacityField = new TextField();
    private final TextField priceField = new TextField();
    private final ComboBox<String> typeBox = new ComboBox<>();

    private static final String TENT = LanguageManager.getInstance().getString("txt.tent");
    private static final String BACKPACK = LanguageManager.getInstance().getString("txt.backpack");
    private static final String OTHER = LanguageManager.getInstance().getString("txt.otherGear");

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

        // Form Layout
        GridPane grid = createGridPane();;

        // ComboBox
        typeBox.getItems().addAll(TENT, BACKPACK, OTHER);
        typeBox.setValue(TENT);
        typeBox.setMaxWidth(Double.MAX_VALUE);

        // Grid layout
        grid.add(new Label(LanguageManager.getInstance().getString("txt.modelName")), 0, 0);
        grid.add(modelField, 1, 0);
        modelField.setPromptText(LanguageManager.getInstance().getString("txt.modelNamePrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.type")), 0, 1);
        grid.add(typeBox, 1, 1);

        grid.add(new Label(LanguageManager.getInstance().getString("table.capacity")), 0, 2);
        grid.add(capacityField, 1, 2);
        capacityField.setPromptText(LanguageManager.getInstance().getString("txt.capacityPrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.dailyPrice")), 0, 3);
        grid.add(priceField, 1, 3);
        priceField.setPromptText(LanguageManager.getInstance().getString("txt.dailyPricePrompt"));

        getDialogPane().setContent(grid);

        // Focus
        Platform.runLater(modelField::requestFocus);

        // Enable/disable button
        Button addButton = (Button) getDialogPane().lookupButton(addButtonType);
        addButton.disableProperty().bind(
                modelField.textProperty().isEmpty()
                        .or(capacityField.textProperty().isEmpty())
                        .or(priceField.textProperty().isEmpty())
        );

        setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {

                // New ID.
                // Pass 0 as ID. The Database (Auto-Increment) will assign the real ID upon save.
                return new Gear(
                        0,
                        modelField.getText().trim(),
                        typeBox.getValue(),
                        capacityField.getText().trim(),
                        Double.parseDouble(priceField.getText().trim())
                );
            }
            return null;
        });
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPadding(new Insets(20,20,10,10));
        return grid;
    }
}