package com.nilsson.ui.views;

import com.nilsson.entity.Vehicle;
import com.nilsson.exception.ItemActiveException;
import com.nilsson.ui.ServiceContainer;
import com.nilsson.ui.dialogs.AddVehicleDialog;
import com.nilsson.ui.dialogs.EditVehicleDialog;
import com.nilsson.util.LanguageManager;
import com.nilsson.service.InventoryService;
import com.nilsson.ui.UIUtil;
import javafx.beans.property.SimpleStringProperty;
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

import java.util.Optional;

public class VehicleView extends VBox {

    private final TableView<Vehicle> vehicleTable = new TableView<>();
    private final ObservableList<Vehicle> masterVehicleData = FXCollections.observableArrayList();
    private final InventoryService inventoryService;
    private final TextField searchField = new TextField();
    private FilteredList<Vehicle> filteredData;

    public VehicleView(ServiceContainer services) {
        this.inventoryService = services.getInventoryService();

        this.getStyleClass().add("content-view");
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(vehicleTable, Priority.ALWAYS);

        Label title = new Label(LanguageManager.getInstance().getString("txt.availableVehicles"));
        title.getStyleClass().add("content-title");

        searchField.setPromptText(LanguageManager.getInstance().getString("txt.searchVehicles"));
        searchField.setMaxWidth(360);

        loadMasterData();
        initializeTable();

        HBox buttonBar = createButtonBar();

        this.getChildren().addAll(title, buttonBar, searchField, vehicleTable);
    }

    private void initializeTable() {
        vehicleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Vehicle, String> makeCol = new TableColumn<>(LanguageManager.getInstance().getString("table.make"));
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));

        TableColumn<Vehicle, String> modelCol = new TableColumn<>(LanguageManager.getInstance().getString("table.model"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Vehicle, String> typeCol = new TableColumn<>(LanguageManager.getInstance().getString("table.type"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Vehicle, String> yearCol = new TableColumn<>(LanguageManager.getInstance().getString("table.year"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Vehicle, String> capacityCol = new TableColumn<>(LanguageManager.getInstance().getString("table.capacity"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        TableColumn<Vehicle, String> priceCol = new TableColumn<>(LanguageManager.getInstance().getString("table.dailyPrice"));
        priceCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCost() != null) {
                return new SimpleStringProperty(cellData.getValue().getCost().toString() + " SEK");
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Vehicle, Boolean> rentedCol = new TableColumn<>(LanguageManager.getInstance().getString("table.status"));
        rentedCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleBooleanProperty(cell.getValue().isRented()));
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
                        setStyle("-fx-text-fill: #e69d67;");
                    }
                }
            }
        });

        vehicleTable.getColumns().addAll(makeCol, modelCol, typeCol, yearCol, capacityCol, priceCol, rentedCol);
        vehicleTable.setItems(masterVehicleData);

        filteredData = new FilteredList<>(masterVehicleData, p -> true);
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
        SortedList<Vehicle> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(vehicleTable.comparatorProperty());
        vehicleTable.setItems(sortedData);
    }

    private void loadMasterData() {
        try {
            masterVehicleData.setAll(inventoryService.getAllVehicles());
        } catch (Exception e) {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.databaseError"),
                    LanguageManager.getInstance().getString("error.couldNotLoadVehicles"),
                    e.getMessage()
            );
        }
    }

    public void refreshData() {
        loadMasterData();
    }

    private void handleAdd() {
        AddVehicleDialog dialog = new AddVehicleDialog();
        Optional<Vehicle> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                inventoryService.saveVehicle(result.get());
                loadMasterData();
                UIUtil.showInfoAlert(LanguageManager.getInstance().getString(
                        "msg.success"),
                        null,
                        LanguageManager.getInstance().getString("txt.vehicleAddedSuccess"));
            } catch (Exception e) {
                UIUtil.showErrorAlert(
                        LanguageManager.getInstance().getString("error.error"),
                        LanguageManager.getInstance().getString("error.couldNotSaveVehicle"),
                        e.getMessage());
            }
        }
    }

    private void handleEdit() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();

        if (selectedVehicle != null) {
            EditVehicleDialog dialog = new EditVehicleDialog(selectedVehicle);
            Optional<Vehicle> result = dialog.showAndWait();

            if (result.isPresent()) {
                try {
                    inventoryService.updateVehicle(result.get());
                    loadMasterData();
                    UIUtil.showInfoAlert(LanguageManager.getInstance().getString(
                            "msg.success"),
                            null,
                            LanguageManager.getInstance().getString("txt.vehicleAddedSuccess"));
                } catch (Exception e) {
                    UIUtil.showErrorAlert(
                            LanguageManager.getInstance().getString("error.error"),
                            LanguageManager.getInstance().getString("error.couldNotSaveVehicle"),
                            e.getMessage());
                }
            }
        }
    }

    private void handleRemoveVehicle() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            showSelectionError("error.pleaseSelectRemoveItem");
            return;
        }

        if (UIUtil.showConfirmationAlert(LanguageManager.getInstance().getString("confirm.removal"), null, "Delete " + selectedVehicle.getModel() + "?")) {
            try {
                inventoryService.deleteVehicle(selectedVehicle);
                masterVehicleData.remove(selectedVehicle);
            } catch (ItemActiveException e) {
                UIUtil.showErrorAlert(
                        LanguageManager.getInstance().getString("error.validation"),
                        LanguageManager.getInstance().getString("error.couldNotRemoveVehicle"),
                        e.getMessage()
                );
            } catch (Exception e) {
                UIUtil.showErrorAlert(
                        LanguageManager.getInstance().getString("error.databaseError"),
                        LanguageManager.getInstance().getString("error.removalFailed"),
                        e.getMessage()
                );
            }
        }
    }

    private void showSelectionError(String contentKey) {
        UIUtil.showErrorAlert(
                LanguageManager.getInstance().getString("error.noItemSelected"),
                null,
                LanguageManager.getInstance().getString(contentKey));
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
        btnRemove.setOnAction(e -> handleRemoveVehicle());

        Button btnRefresh = new Button();
        btnRefresh.setGraphic(new FontIcon(FontAwesome.REFRESH));
        btnRefresh.getStyleClass().add("action-button");
        btnRefresh.setOnAction(e -> refreshData());

        HBox box = new HBox(10, btnAdd, btnEdit, btnRemove, btnRefresh);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}