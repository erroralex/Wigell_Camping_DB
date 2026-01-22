package com.nilsson.ui.views;

import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Gear;
import com.nilsson.registries.Inventory;
import com.nilsson.service.InventoryService;
import com.nilsson.ui.UIUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class GearView extends VBox {

    private final TableView<Gear> gearTable = new TableView<>();
    private final ObservableList<Gear> masterGearData = FXCollections.observableArrayList();
    private final InventoryService inventoryService = new InventoryService();
    private final TextField searchField = new TextField();
    private FilteredList<Gear> filteredData;

    public GearView() {
        this.getStyleClass().add("content-view");
        this.setPadding(new Insets(20));
        this.setSpacing(20);
        this.setAlignment(Pos.TOP_LEFT);

        VBox.setVgrow(gearTable, Priority.ALWAYS);

        Label title = new Label(LanguageManager.getInstance().getString("txt.availableGear"));
        title.getStyleClass().add("content-title");

        searchField.setPromptText(LanguageManager.getInstance().getString("txt.searchGear"));
        searchField.setMaxWidth(360);

        loadMasterData();
        initializeTable();

        HBox buttonBar = createButtonBar();

        this.getChildren().addAll(title, buttonBar, searchField, gearTable);
    }

    private void initializeTable() {
        gearTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Gear, String> typeCol = new TableColumn<>(LanguageManager.getInstance().getString("table.type"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Gear, String> modelCol = new TableColumn<>(LanguageManager.getInstance().getString("table.model"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Gear, String> capacityCol = new TableColumn<>(LanguageManager.getInstance().getString("table.capacity"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        TableColumn<Gear, Double> priceCol = new TableColumn<>(LanguageManager.getInstance().getString("table.dailyPrice"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("dailyPrice"));

        TableColumn<Gear, Boolean> rentedCol = new TableColumn<>(LanguageManager.getInstance().getString("table.status"));
        rentedCol.setCellValueFactory(new PropertyValueFactory<>("rented"));
        rentedCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean isRented, boolean empty) {
                super.updateItem(isRented, empty);
                if (empty || isRented == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (isRented) {
                        setText(LanguageManager.getInstance().getString("status.rented"));
                        setStyle("-fx-text-fill: #8B0000; -fx-font-weight: bold;");
                    } else {
                        setText(LanguageManager.getInstance().getString("status.available"));
                        setStyle("-fx-text-fill: #008B00;");
                    }
                }
            }
        });

        gearTable.getColumns().addAll(typeCol, modelCol, capacityCol, priceCol, rentedCol);

        filteredData = new FilteredList<>(masterGearData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(gear -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return gear.getModel().toLowerCase().contains(lowerCaseFilter) ||
                        gear.getType().toLowerCase().contains(lowerCaseFilter) ||
                        gear.getCapacity().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(gear.getDailyPrice()).contains(lowerCaseFilter);
            });
        });

        SortedList<Gear> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(gearTable.comparatorProperty());
        gearTable.setItems(sortedData);
    }

    private void loadMasterData() {
        // Calls Inventory -> DAO directly, so data is always fresh
        List<Gear> gearList = Inventory.getInstance().getGearList();
        masterGearData.setAll(gearList);
    }

    public void refreshData() {
        // REMOVED: Inventory.getInstance().refreshInventory();
        loadMasterData();
    }

    private void handleAddGear() {
        Gear newGear = inventoryService.handleAddGear();
        if (newGear != null) {
            refreshData();
        }
    }

    private void handleEditGear() {
        Gear selectedGear = gearTable.getSelectionModel().getSelectedItem();

        if (selectedGear != null) {
            inventoryService.handleEditGear(selectedGear);
            gearTable.refresh();
        } else {
            showSelectionError("error.pleaseSelectEditItem");
        }
    }

    private void handleRemoveGear() {
        Gear selectedGear = gearTable.getSelectionModel().getSelectedItem();

        if (selectedGear != null) {
            boolean confirmed = UIUtil.showConfirmationAlert(
                    LanguageManager.getInstance().getString("confirm.removal"),
                    LanguageManager.getInstance().getString("confirm.confirm"),
                    LanguageManager.getInstance().getString("confirm.selected") + " " + selectedGear.getModel() + "?");

            if (confirmed) {
                if (inventoryService.handleRemoveGear(selectedGear)) {
                    masterGearData.remove(selectedGear);
                }
            }
        } else {
            showSelectionError("error.pleaseSelectRemoveItem");
        }
    }

    private void showSelectionError(String contentKey) {
        UIUtil.showErrorAlert(
                LanguageManager.getInstance().getString("error.noItemSelected"),
                LanguageManager.getInstance().getString("error.selectionRequired"),
                LanguageManager.getInstance().getString(contentKey));
    }

    private HBox createButtonBar() {
        Button btnAdd = new Button(LanguageManager.getInstance().getString("btn.addGear"));
        btnAdd.getStyleClass().add("action-button");
        btnAdd.setOnAction(e -> handleAddGear());

        Button btnEdit = new Button(LanguageManager.getInstance().getString("btn.editGear"));
        btnEdit.getStyleClass().add("action-button");
        btnEdit.setOnAction(e -> handleEditGear());

        Button btnRemove = new Button(LanguageManager.getInstance().getString("btn.removeGear"));
        btnRemove.getStyleClass().add("action-button");
        btnRemove.setOnAction(e -> handleRemoveGear());

        Button btnRefresh = new Button();
        btnRefresh.setGraphic(new FontIcon(FontAwesome.REFRESH));
        btnRefresh.getStyleClass().add("action-button");
        btnRefresh.setOnAction(e -> refreshData());

        HBox buttonBar = new HBox(10, btnAdd, btnEdit, btnRemove, btnRefresh);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        return buttonBar;
    }
}