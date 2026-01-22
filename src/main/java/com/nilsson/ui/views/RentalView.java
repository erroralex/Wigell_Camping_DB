package com.nilsson.ui.views;

import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Rental;
import com.nilsson.registries.RentalRegistry;
import com.nilsson.service.RentalService;
import com.nilsson.ui.UIUtil;
import com.nilsson.ui.dialogs.NewRentalDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RentalView extends VBox {

    private final TableView<Rental> rentalsTable = new TableView<>();
    private final ObservableList<Rental> masterRentalData = FXCollections.observableArrayList();
    private final RentalService rentalService = new RentalService();
    private final Runnable onDataUpdate;

    public RentalView(Runnable onDataUpdate) {
        this.onDataUpdate = onDataUpdate;

        // Layout & CSS
        this.getStyleClass().add("content-view");
        this.setPadding(new Insets(20));
        this.setSpacing(20);
        this.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(rentalsTable, Priority.ALWAYS);

        Label title = new Label(LanguageManager.getInstance().getString("txt.rentalManagement"));
        title.getStyleClass().add("content-title");

        // Buttons
        Button btnNewRental = new Button(LanguageManager.getInstance().getString("btn.newRental"));
        btnNewRental.getStyleClass().add("action-button");
        btnNewRental.setOnAction(e -> handleNewRental());

        Button btnReturn = new Button(LanguageManager.getInstance().getString("btn.returnRental"));
        btnReturn.getStyleClass().add("action-button");
        btnReturn.setOnAction(e -> handleReturnRental());

        Button btnRefresh = new Button();
        btnRefresh.setGraphic(new FontIcon(FontAwesome.REFRESH));
        btnRefresh.getStyleClass().add("action-button");
        btnRefresh.setOnAction(e -> refreshData());

        HBox buttonBar = new HBox(10, btnNewRental, btnReturn, btnRefresh);

        loadData();
        initializeTable();

        this.getChildren().addAll(title, buttonBar, rentalsTable);
    }

    public RentalView() {
        this(null);
    }

    private void initializeTable() {
        rentalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Rental, String> memberCol = new TableColumn<>(LanguageManager.getInstance().getString("table.member"));
        memberCol.setCellValueFactory(cellData -> {
            Rental rental = cellData.getValue();
            if (rental.getMember() != null) {
                return new SimpleStringProperty(rental.getMember().getFirstName() + " " + rental.getMember().getLastName());
            }
            return new SimpleStringProperty("Unknown ID: " + rental.getMemberId());
        });

        TableColumn<Rental, String> itemCol = new TableColumn<>(LanguageManager.getInstance().getString("table.item"));
        itemCol.setCellValueFactory(cellData -> {
            Rental rental = cellData.getValue();
            return new SimpleStringProperty(rentalService.getItemNameFromId(rental.getItemId(), rental.getItemType()));
        });

        TableColumn<Rental, LocalDate> dateCol = new TableColumn<>(LanguageManager.getInstance().getString("table.date"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Rental, Integer> daysCol = new TableColumn<>(LanguageManager.getInstance().getString("table.days"));
        daysCol.setCellValueFactory(new PropertyValueFactory<>("rentalDays"));

        rentalsTable.getColumns().addAll(memberCol, itemCol, dateCol, daysCol);
        rentalsTable.setItems(masterRentalData);
    }

    private void loadData() {
        List<Rental> allRentals = RentalRegistry.getInstance().getRentals();
        List<Rental> activeRentals = allRentals.stream()
                .filter(r -> r.getStatus() == null || "ACTIVE".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());
        masterRentalData.setAll(activeRentals);
    }

    public void refreshData() {
        loadData();
    }

    private void handleNewRental() {
        NewRentalDialog dialog = new NewRentalDialog();

        dialog.showAndWait().ifPresent(result -> {
            IRentable selectedItem = result.getSelectedGear();
            if (selectedItem == null) {
                selectedItem = result.getSelectedVehicle();
            }

            if (selectedItem != null) {
                boolean success = rentalService.handleNewRental(
                        result.getSelectedMember(),
                        selectedItem,
                        result.getStartDate(),
                        result.getDays()
                );

                if (success) {
                    refreshData();
                    if (onDataUpdate != null) {
                        onDataUpdate.run();
                    }

                    String memberName = result.getSelectedMember().getFirstName() + " " + result.getSelectedMember().getLastName();
                    String itemName = selectedItem.getItemName();

                    String details = LanguageManager.getInstance().getString("table.member") + ": " + memberName + "\n" +
                            LanguageManager.getInstance().getString("table.item") + ": " + itemName + "\n" +
                            LanguageManager.getInstance().getString("table.days") + ": " + result.getDays();

                    UIUtil.showInfoAlert(
                            LanguageManager.getInstance().getString("info.success"),
                            LanguageManager.getInstance().getString("info.rentalAdded"),
                            details
                    );
                } else {
                    // FIXED: Replaced hardcoded strings
                    UIUtil.showErrorAlert(
                            LanguageManager.getInstance().getString("error.genericTitle"),
                            LanguageManager.getInstance().getString("error.operationFailed"),
                            LanguageManager.getInstance().getString("error.createRentalFailed")
                    );
                }
            }
        });
    }

    private void handleReturnRental() {
        Rental selected = rentalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.selectionRequired"),
                    LanguageManager.getInstance().getString("error.noItemSelected"),
                    LanguageManager.getInstance().getString("error.pleaseSelectRentalReturn"));
            return;
        }

        boolean success = rentalService.handleReturnRental(selected);
        if (success) {
            // FIXED: Replaced hardcoded strings
            UIUtil.showInfoAlert(
                    LanguageManager.getInstance().getString("msg.success"),
                    LanguageManager.getInstance().getString("info.rentalReturnedHeader"),
                    LanguageManager.getInstance().getString("info.rentalReturnedMsg"));

            refreshData();
            if (onDataUpdate != null) {
                onDataUpdate.run();
            }
        } else {
            // FIXED: Replaced hardcoded strings
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.returnFailed"),
                    LanguageManager.getInstance().getString("error.returnFailedHeader"),
                    LanguageManager.getInstance().getString("error.returnFailedMsg"));
        }
    }
}