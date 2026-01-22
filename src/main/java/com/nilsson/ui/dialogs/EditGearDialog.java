package com.nilsson.ui.dialogs;

import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Gear;
import com.nilsson.ui.UIUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class EditGearDialog extends Dialog<Gear> {

    private final TextField modelField = new TextField();
    private final TextField capacityField = new TextField();
    private final TextField priceField = new TextField();
    private final ComboBox<String> typeBox = new ComboBox<>();

    private static final String TENT = LanguageManager.getInstance().getString("txt.tent");
    private static final String BACKPACK = LanguageManager.getInstance().getString("txt.backpack");
    private static final String OTHER = LanguageManager.getInstance().getString("txt.otherGear");

    private final Gear gearToEdit;

    public EditGearDialog(Gear gearToEdit) {

        this.gearToEdit = gearToEdit;

        setTitle(LanguageManager.getInstance().getString("txt.editGearTitle"));
        setHeaderText(LanguageManager.getInstance().getString("txt.editGearHeader") + gearToEdit.getModel());

        // Apply theme and mouse-drag
        this.setOnShowing(dialogEvent -> {
            UIUtil.applyDialogSetup(this);
        });

        ButtonType saveButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.save"),
                ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.cancel"),
                ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().clear();
        getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Form Layout
        GridPane grid = createGridPane();;

        // ComboBox
        typeBox.getItems().addAll(TENT, BACKPACK, OTHER);
        typeBox.setValue(gearToEdit.getType());
        typeBox.setMaxWidth(Double.MAX_VALUE);

        // Grid layout
        grid.add(new Label(LanguageManager.getInstance().getString("txt.modelName")), 0, 0);
        grid.add(modelField, 1, 0);
        modelField.setText(gearToEdit.getModel());
        modelField.setPromptText(LanguageManager.getInstance().getString("txt.modelNamePrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.type")), 0, 1);
        grid.add(typeBox, 1, 1);

        grid.add(new Label(LanguageManager.getInstance().getString("table.capacity")), 0, 2);
        grid.add(capacityField, 1, 2);
        capacityField.setText(gearToEdit.getCapacity());
        capacityField.setPromptText(LanguageManager.getInstance().getString("txt.capacityPrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.dailyPrice")), 0, 3);
        grid.add(priceField, 1, 3);
        priceField.setText(String.valueOf(gearToEdit.getDailyPrice()));
        priceField.setPromptText(LanguageManager.getInstance().getString("txt.dailyPricePrompt"));

        getDialogPane().setContent(grid);

        // Focus
        Platform.runLater(modelField::requestFocus);

        // Enable/disable button
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.disableProperty().bind(
                modelField.textProperty().isEmpty()
                        .or(capacityField.textProperty().isEmpty())
                        .or(priceField.textProperty().isEmpty())
        );

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                gearToEdit.setModel(modelField.getText().trim());
                gearToEdit.setType(typeBox.getValue());
                gearToEdit.setCapacity(capacityField.getText().trim());
                gearToEdit.setDailyPrice(Double.parseDouble(priceField.getText().trim()));
                return gearToEdit;
            }
            // If Cancel is clicked
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