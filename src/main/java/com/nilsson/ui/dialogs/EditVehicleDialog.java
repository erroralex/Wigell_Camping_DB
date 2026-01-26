package com.nilsson.ui.dialogs;

import com.nilsson.entity.Vehicle;
import com.nilsson.util.LanguageManager;
import com.nilsson.ui.UIUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.math.BigDecimal;

public class EditVehicleDialog extends Dialog<Vehicle> {

    private final TextField makeField = new TextField();
    private final TextField modelField = new TextField();
    private final TextField yearField = new TextField();
    private final TextField capacityField = new TextField();
    private final TextField priceField = new TextField();
    private final ComboBox<String> typeBox = new ComboBox<>();

    private static final String CARAVAN = LanguageManager.getInstance().getString("txt.caravan");
    private static final String MOTORHOME = LanguageManager.getInstance().getString("txt.motorhome");
    private static final String CAMPERVAN = LanguageManager.getInstance().getString("txt.campervan");

    private final Vehicle vehicleToEdit;

    public EditVehicleDialog(Vehicle vehicleToEdit) {

        this.vehicleToEdit = vehicleToEdit;

        setTitle(LanguageManager.getInstance().getString("txt.editVehicleTitle"));
        setHeaderText(LanguageManager.getInstance().getString("txt.editVehicleHeader") + " " + vehicleToEdit.getMake() + " " +
                vehicleToEdit.getModel());

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
        typeBox.getItems().addAll(CARAVAN, CAMPERVAN, MOTORHOME);
        typeBox.setValue(vehicleToEdit.getType());
        typeBox.setMaxWidth(Double.MAX_VALUE);

        // Grid layout
        grid.add(new Label(LanguageManager.getInstance().getString("table.make")), 0, 0);
        grid.add(makeField, 1, 0);
        makeField.setText(vehicleToEdit.getMake());
        makeField.setPromptText(LanguageManager.getInstance().getString("txt.makePrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.model")), 0, 1);
        grid.add(modelField, 1, 1);
        modelField.setText(vehicleToEdit.getModel());
        modelField.setPromptText(LanguageManager.getInstance().getString("txt.modelPrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.type")), 0, 2);
        grid.add(typeBox, 1, 2);

        grid.add(new Label(LanguageManager.getInstance().getString("table.year")), 0, 3);
        grid.add(yearField, 1, 3);
        yearField.setText(vehicleToEdit.getYear());
        yearField.setPromptText(LanguageManager.getInstance().getString("txt.yearPrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.capacity")), 0, 4);
        grid.add(capacityField, 1, 4);
        capacityField.setText(vehicleToEdit.getCapacity());
        capacityField.setPromptText(LanguageManager.getInstance().getString("txt.capacityPrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.dailyPrice")), 0, 5);
        grid.add(priceField, 1, 5);
        priceField.setText(String.valueOf(vehicleToEdit.getCost()));
        priceField.setPromptText(LanguageManager.getInstance().getString("txt.dailyPricePrompt"));

        getDialogPane().setContent(grid);

        // Focus
        Platform.runLater(makeField::requestFocus);

        // Enable/disable button
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.disableProperty().bind(
                modelField.textProperty().isEmpty()
                        .or(capacityField.textProperty().isEmpty())
                        .or(priceField.textProperty().isEmpty())
                        .or(yearField.textProperty().isEmpty())
                        .or(makeField.textProperty().isEmpty())
        );

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                vehicleToEdit.setModel(modelField.getText().trim());
                vehicleToEdit.setMake(makeField.getText().trim());
                vehicleToEdit.setType(typeBox.getValue());
                vehicleToEdit.setYear(yearField.getText().trim());
                vehicleToEdit.setCapacity(capacityField.getText().trim());
                vehicleToEdit.setCost(new BigDecimal(priceField.getText().trim()));
                return vehicleToEdit;
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