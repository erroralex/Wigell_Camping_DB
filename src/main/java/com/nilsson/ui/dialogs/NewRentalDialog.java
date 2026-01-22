package com.nilsson.ui.dialogs;

import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Member;
import com.nilsson.model.NewRentalResult;
import com.nilsson.entity.Gear;
import com.nilsson.registries.Inventory;
import com.nilsson.registries.MemberRegistry;
import com.nilsson.ui.UIUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;

public class NewRentalDialog extends Dialog<NewRentalResult> {

    private final ComboBox<Member> memberBox = new ComboBox<>();
    private final ComboBox<String> itemTypeBox = new ComboBox<>();
    private final ComboBox<Gear> gearBox = new ComboBox<>();
    private final ComboBox<RecreationalVehicle> vehicleBox = new ComboBox<>();
    private final DatePicker startDatePicker = new DatePicker(LocalDate.now());
    private final TextField daysField = new TextField();

    // Changed from static to instance variables to support language switching
    private final String typeGear;
    private final String typeVehicle;

    public NewRentalDialog() {
        // 1. Load strings inside the constructor to ensure they are current
        this.typeGear = LanguageManager.getInstance().getString("txt.newRentalTypeGear");
        this.typeVehicle = LanguageManager.getInstance().getString("txt.newRentalTypeVehicle");

        setTitle(LanguageManager.getInstance().getString("txt.newRentalTitle"));
        setHeaderText(LanguageManager.getInstance().getString("txt.newRentalHeader"));

        // Theme/drag support
        this.setOnShowing(event -> UIUtil.applyDialogSetup(this));

        ButtonType createButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.createRental"),
                ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.cancel"),
                ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);

        GridPane grid = createGridPane();

        // Member
        List<Member> members = MemberRegistry.getInstance().getMembers();
        memberBox.getItems().addAll(members);
        memberBox.setMaxWidth(Double.MAX_VALUE);
        memberBox.setPromptText(LanguageManager.getInstance().getString("txt.selectMember"));

        // Show member
        memberBox.setCellFactory(cb -> new ListCell<Member>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFirstName() + " " + item.getLastName() + " (ID: " + item.getId() + ")");
                }
            }
        });

        memberBox.setButtonCell(memberBox.getCellFactory().call(null));

        // Item type
        itemTypeBox.getItems().addAll(typeGear, typeVehicle);
        itemTypeBox.setValue(typeGear);
        itemTypeBox.setMaxWidth(Double.MAX_VALUE);

        // Item combos
        loadAvailableGear();
        loadAvailableVehicles();

        gearBox.setMaxWidth(Double.MAX_VALUE);
        vehicleBox.setMaxWidth(Double.MAX_VALUE);

        // Display items
        gearBox.setCellFactory(cb -> new ListCell<Gear>() {
            @Override
            protected void updateItem(Gear item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getModel() + " (" + item.getType() + ")");
            }
        });

        gearBox.setButtonCell(gearBox.getCellFactory().call(null));

        vehicleBox.setCellFactory(cb -> new ListCell<RecreationalVehicle>() {
            @Override
            protected void updateItem(RecreationalVehicle item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getMake() + " " + item.getModel() + " (" + item.getType() + ")");
            }
        });

        vehicleBox.setButtonCell(vehicleBox.getCellFactory().call(null));

        // Days
        daysField.setPromptText(LanguageManager.getInstance().getString("txt.daysPrompt"));

        // Layout
        grid.add(new Label(LanguageManager.getInstance().getString("table.member")), 0, 0);
        grid.add(memberBox, 1, 0);

        grid.add(new Label(LanguageManager.getInstance().getString("txt.itemType")), 0, 1);
        grid.add(itemTypeBox, 1, 1);

        grid.add(new Label(LanguageManager.getInstance().getString("txt.itemGear")), 0, 2);
        grid.add(gearBox, 1, 2);

        grid.add(new Label(LanguageManager.getInstance().getString("txt.itemVehicle")), 0, 3);
        grid.add(vehicleBox, 1, 3);

        grid.add(new Label(LanguageManager.getInstance().getString("table.startDate")), 0, 4);
        grid.add(startDatePicker, 1, 4);

        grid.add(new Label(LanguageManager.getInstance().getString("txt.numberOfDays")), 0, 5);
        grid.add(daysField, 1, 5);

        getDialogPane().setContent(grid);

        // Toggle which item combo is active
        updateItemComboVisibility();
        itemTypeBox.valueProperty().addListener((obs, oldV, newV) -> updateItemComboVisibility());

        // Focus
        Platform.runLater(memberBox::requestFocus);

        // Enable/disable button
        Button createButton = (Button) getDialogPane().lookupButton(createButtonType);
        createButton.disableProperty().bind
                (memberBox.valueProperty().isNull().or
                        (startDatePicker.valueProperty().isNull()).or
                        (daysField.textProperty().isEmpty()));

        setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Member member = memberBox.getValue();
                String type = itemTypeBox.getValue();

                Gear gear = null;
                RecreationalVehicle vehicle = null;

                if (typeGear.equals(type)) {
                    gear = gearBox.getValue();
                    if (gear == null) {
                        UIUtil.showErrorAlert(
                                LanguageManager.getInstance().getString("error.missingItem"),
                                LanguageManager.getInstance().getString("error.noItemSelected"),
                                LanguageManager.getInstance().getString("error.pleaseSelectItem"));
                        return null;
                    }
                } else {
                    vehicle = vehicleBox.getValue();
                    if (vehicle == null) {
                        UIUtil.showErrorAlert(
                                LanguageManager.getInstance().getString("error.missingItem"),
                                LanguageManager.getInstance().getString("error.noItemSelected"),
                                LanguageManager.getInstance().getString("error.pleaseSelectItem"));
                        return null;
                    }
                }

                int days;
                try {
                    days = Integer.parseInt(daysField.getText().trim());
                    if (days <= 0) {
                        throw new NumberFormatException(LanguageManager.getInstance().getString("error.numFormat"));
                    }
                } catch (NumberFormatException ex) {
                    UIUtil.showErrorAlert(
                            LanguageManager.getInstance().getString("error.invalidDays"),
                            LanguageManager.getInstance().getString("error.input"),
                            LanguageManager.getInstance().getString("error.daysInput"));
                    return null;
                }

                return new NewRentalResult(member, gear, vehicle, startDatePicker.getValue(), days);
            }
            return null;
        });
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPadding(new Insets(20, 20, 10, 10));
        return grid;
    }

    private void updateItemComboVisibility() {
        // Use the instance variable 'typeGear'
        boolean gearSelected = typeGear.equals(itemTypeBox.getValue());
        gearBox.setDisable(!gearSelected);
        vehicleBox.setDisable(gearSelected);
        if (gearSelected) {
            vehicleBox.setValue(null);
        } else {
            gearBox.setValue(null);
        }
    }

    private void loadAvailableGear() {
        gearBox.getItems().setAll(Inventory.getInstance().getAvailableGearList());
    }

    private void loadAvailableVehicles() {
        vehicleBox.getItems().setAll(Inventory.getInstance().getAvailableRecreationalVehicleList());
    }
}