package com.nilsson.ui.views;

import com.nilsson.entity.*;
import com.nilsson.model.NewRentalResult;
import com.nilsson.service.InventoryService;
import com.nilsson.service.MemberService;
import com.nilsson.service.RentalService;
import com.nilsson.ui.ServiceContainer;
import com.nilsson.ui.UIUtil;
import com.nilsson.ui.dialogs.NewRentalDialog;
import com.nilsson.util.LanguageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RentalView extends VBox {

    private final TableView<Rental> rentalsTable = new TableView<>();
    private final ObservableList<Rental> masterRentalData = FXCollections.observableArrayList();

    private final RentalService rentalService;
    private final MemberService memberService;
    private final InventoryService inventoryService;

    private final Runnable onDataUpdate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Constructor Injection
    public RentalView(ServiceContainer services, Runnable onDataUpdate) {
        this.rentalService = services.getRentalService();
        this.memberService = services.getMemberService();
        this.inventoryService = services.getInventoryService();
        this.onDataUpdate = onDataUpdate;

        // Layout & Styling
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

        initializeTable();
        loadData();

        this.getChildren().addAll(title, buttonBar, rentalsTable);
    }

    private void initializeTable() {
        rentalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Member
        TableColumn<Rental, String> memberCol = new TableColumn<>(LanguageManager.getInstance().getString("table.member"));
        memberCol.setCellValueFactory(cellData -> {
            Rental r = cellData.getValue();
            if (r.getMember() != null) {
                return new SimpleStringProperty(r.getMember().getFirstName() + " " + r.getMember().getLastName());
            }
            return new SimpleStringProperty("Unknown Member");
        });

        // Item
        TableColumn<Rental, String> itemCol = new TableColumn<>(LanguageManager.getInstance().getString("table.item"));
        itemCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRentalType() + " (ID: " + cellData.getValue().getRentalObjectId() + ")")
        );

        // Date
        TableColumn<Rental, String> dateCol = new TableColumn<>(LanguageManager.getInstance().getString("table.date"));
        dateCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartTime() != null) {
                return new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter));
            }
            return new SimpleStringProperty("");
        });

        // Status
        TableColumn<Rental, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            Rental r = cellData.getValue();
            if (r.getEndTime() != null) {
                return new SimpleStringProperty("Returned");
            } else {
                long days = Duration.between(r.getStartTime(), LocalDateTime.now()).toDays();
                return new SimpleStringProperty("Active (" + days + " days)");
            }
        });

        rentalsTable.getColumns().addAll(memberCol, itemCol, dateCol, statusCol);
        rentalsTable.setItems(masterRentalData);
    }

    private void loadData() {
        List<Rental> allRentals = rentalService.getAllRentals();
        List<Rental> activeRentals = allRentals.stream()
                .filter(r -> r.getEndTime() == null)
                .toList();

        masterRentalData.setAll(activeRentals);
    }

    public void refreshData() {
        loadData();
    }

    private void handleNewRental() {
        NewRentalDialog dialog = new NewRentalDialog(memberService, inventoryService);
        Optional<NewRentalResult> result = dialog.showAndWait();

        result.ifPresent(res -> {
            try {
                Object item = res.getSelectedItem();
                Long objectId = res.getObjectId();

                rentalService.rentItem(
                        res.getMember(),
                        objectId,
                        res.getType(),
                        LocalDateTime.now()
                );

                refreshData();
                if (onDataUpdate != null) onDataUpdate.run();

                UIUtil.showInfoAlert(
                        LanguageManager.getInstance().getString("info.success"),
                        "Rental Created",
                        "Success"
                );

            } catch (Exception e) {
                e.printStackTrace();
                UIUtil.showErrorAlert("Error", "Rental Failed", e.getMessage());
            }
        });
    }

    private void handleReturnRental() {
        Rental selected = rentalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.selectionRequired"),
                    "No Selection",
                    "Please select a rental."
            );
            return;
        }

        try {
            rentalService.returnItem(selected);

            UIUtil.showInfoAlert(
                    LanguageManager.getInstance().getString("info.success"),
                    "Returned",
                    "Total Cost: " + selected.getTotalCost()
            );

            refreshData();
            if (onDataUpdate != null) onDataUpdate.run();

        } catch (Exception e) {
            UIUtil.showErrorAlert("Error", "Return Failed", e.getMessage());
        }
    }
}