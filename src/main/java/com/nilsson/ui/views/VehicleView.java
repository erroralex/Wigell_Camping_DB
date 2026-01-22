package com.nilsson.ui.views;

import com.nilsson.util.LanguageManager;
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

public class VehicleView extends VBox {

    private final TableView<RecreationalVehicle> recreationalVehicleTable = new TableView<>();
    private final ObservableList<RecreationalVehicle> masterData = FXCollections.observableArrayList();
    private final InventoryService inventoryService = new InventoryService();
    private final TextField searchField = new TextField();
    private FilteredList<RecreationalVehicle> filteredData;

    public VehicleView() {
        this.getStyleClass().add("content-view");
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(recreationalVehicleTable, Priority.ALWAYS);

        Label title = new Label(LanguageManager.getInstance().getString("txt.availableVehicles"));
        title.getStyleClass().add("content-title");

        searchField.setPromptText(LanguageManager.getInstance().getString("txt.searchVehicles"));
        searchField.setMaxWidth(360);

        loadMasterData();
        initializeTable();

        HBox buttonBar = createButtonBar();

        this.getChildren().addAll(title, buttonBar, searchField, recreationalVehicleTable);
    }

    private void initializeTable() {
        recreationalVehicleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<RecreationalVehicle, String> makeCol = new TableColumn<>(LanguageManager.getInstance().getString("table.make"));
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));

        TableColumn<RecreationalVehicle, String> modelCol = new TableColumn<>(LanguageManager.getInstance().getString("table.model"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<RecreationalVehicle, String> typeCol = new TableColumn<>(LanguageManager.getInstance().getString("table.type"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<RecreationalVehicle, String> yearCol = new TableColumn<>(LanguageManager.getInstance().getString("table.year"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<RecreationalVehicle, String> capacityCol = new TableColumn<>(LanguageManager.getInstance().getString("table.capacity"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        TableColumn<RecreationalVehicle, Double> priceCol = new TableColumn<>(LanguageManager.getInstance().getString("table.dailyPrice"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("dailyPrice"));

        TableColumn<RecreationalVehicle, Boolean> rentedCol = new TableColumn<>(LanguageManager.getInstance().getString("table.status"));
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

        recreationalVehicleTable.getColumns().addAll(makeCol, modelCol, typeCol, yearCol, capacityCol, priceCol, rentedCol);
        recreationalVehicleTable.setItems(masterData);

        filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(vehicle -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return vehicle.getMake().toLowerCase().contains(lowerCaseFilter) ||
                        vehicle.getModel().toLowerCase().contains(lowerCaseFilter) ||
                        vehicle.getType().toLowerCase().contains(lowerCaseFilter) ||
                        vehicle.getYear().contains(lowerCaseFilter);
            });
        });
        SortedList<RecreationalVehicle> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(recreationalVehicleTable.comparatorProperty());
        recreationalVehicleTable.setItems(sortedData);
    }

    private void loadMasterData() {
        // Calls Inventory -> DAO directly, always fresh
        List<RecreationalVehicle> list = Inventory.getInstance().getRecreationalVehicleList();
        masterData.setAll(list);
    }

    public void refreshData() {
        // REMOVED: Inventory.getInstance().refreshInventory();
        loadMasterData();
    }

    private void handleAdd() {
        RecreationalVehicle newRV = inventoryService.handleAddRecreationalVehicle();
        if(newRV != null) refreshData();
    }

    private void handleEdit() {
        RecreationalVehicle selected = recreationalVehicleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            inventoryService.handleEditRecreationalVehicle(selected);
            recreationalVehicleTable.refresh();
        } else {
            showSelectionError();
        }
    }

    private void handleRemove() {
        RecreationalVehicle selected = recreationalVehicleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (UIUtil.showConfirmationAlert(
                    LanguageManager.getInstance().getString("confirm.removal"),
                    LanguageManager.getInstance().getString("confirm.confirm"),
                    LanguageManager.getInstance().getString("confirm.selected") + " " + selected.getMake())) {

                if (inventoryService.handleRemoveRecreationalVehicle(selected)) {
                    masterData.remove(selected);
                }
            }
        } else {
            showSelectionError();
        }
    }

    private void showSelectionError() {
        UIUtil.showErrorAlert(
                LanguageManager.getInstance().getString("error.noItemSelected"),
                LanguageManager.getInstance().getString("error.selectionRequired"),
                LanguageManager.getInstance().getString("error.pleaseSelectEditItem"));
    }

    private HBox createButtonBar() {
        Button btnAdd = new Button(LanguageManager.getInstance().getString("btn.addVehicle"));
        btnAdd.getStyleClass().add("action-button");
        btnAdd.setOnAction(e -> handleAdd());

        Button btnEdit = new Button(LanguageManager.getInstance().getString("btn.editVehicle"));
        btnEdit.getStyleClass().add("action-button");
        btnEdit.setOnAction(e -> handleEdit());

        Button btnRemove = new Button(LanguageManager.getInstance().getString("btn.removeVehicle"));
        btnRemove.getStyleClass().add("action-button");
        btnRemove.setOnAction(e -> handleRemove());

        Button btnRefresh = new Button();
        btnRefresh.setGraphic(new FontIcon(FontAwesome.REFRESH));
        btnRefresh.getStyleClass().add("action-button");
        btnRefresh.setOnAction(e -> refreshData());

        HBox box = new HBox(10, btnAdd, btnEdit, btnRemove, btnRefresh);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}