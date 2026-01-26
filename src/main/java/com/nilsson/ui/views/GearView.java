package com.nilsson.ui.views;

import com.nilsson.entity.Gear;
import com.nilsson.entity.Tent;
import com.nilsson.service.InventoryService;
import com.nilsson.ui.UIUtil;
import com.nilsson.ui.dialogs.AddGearDialog;
import com.nilsson.ui.dialogs.EditGearDialog;
import com.nilsson.util.LanguageManager;
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

import java.math.BigDecimal;
import java.util.Optional;

public class GearView extends VBox {

    private final TableView<InventoryItemViewModel> gearTable = new TableView<>();
    private final ObservableList<InventoryItemViewModel> masterData = FXCollections.observableArrayList();
    private final InventoryService inventoryService;
    private final TextField searchField = new TextField();
    private FilteredList<InventoryItemViewModel> filteredData;

    public GearView(InventoryService inventoryService) {
        this.inventoryService = inventoryService;

        this.getStyleClass().add("content-view");
        this.setPadding(new Insets(20));
        this.setSpacing(20);
        this.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(gearTable, Priority.ALWAYS);

        Label title = new Label(LanguageManager.getInstance().getString("txt.availableGear"));
        title.getStyleClass().add("content-title");

        searchField.setPromptText(LanguageManager.getInstance().getString("txt.searchGear"));
        searchField.setMaxWidth(360);

        initializeTable();
        loadMasterData(); // Loads both Gear and Tents

        HBox buttonBar = createButtonBar();
        this.getChildren().addAll(title, buttonBar, searchField, gearTable);
    }

    private void initializeTable() {
        gearTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<InventoryItemViewModel, String> typeCol = new TableColumn<>(LanguageManager.getInstance().getString("table.type"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<InventoryItemViewModel, String> modelCol = new TableColumn<>(LanguageManager.getInstance().getString("table.model"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<InventoryItemViewModel, String> capacityCol = new TableColumn<>(LanguageManager.getInstance().getString("table.capacity"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        TableColumn<InventoryItemViewModel, BigDecimal> priceCol = new TableColumn<>(LanguageManager.getInstance().getString("table.dailyPrice"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

        TableColumn<InventoryItemViewModel, Boolean> rentedCol = new TableColumn<>(LanguageManager.getInstance().getString("table.status"));
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
                        setStyle("-fx-text-fill: #e69d67;");
                    }
                }
            }
        });

        gearTable.getColumns().addAll(typeCol, modelCol, capacityCol, priceCol, rentedCol);

        filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return item.getModel().toLowerCase().contains(lower) ||
                        item.getType().toLowerCase().contains(lower) ||
                        item.getCapacity().toLowerCase().contains(lower);
            });
        });

        SortedList<InventoryItemViewModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(gearTable.comparatorProperty());
        gearTable.setItems(sortedData);
    }

    private void loadMasterData() {
        masterData.clear();

        // Fetch Tents and map to ViewModel
        for (Tent t : inventoryService.getAllTents()) {
            masterData.add(new InventoryItemViewModel(
                    t.getId(), t.getModel(), "Tent", t.getCapacity(), t.getCost(), t.isRented(), true, t
            ));
        }

        // Fetch Gear and map to ViewModel
        for (Gear g : inventoryService.getAllGear()) {
            masterData.add(new InventoryItemViewModel(
                    g.getId(), g.getModel(), g.getType(), g.getCapacity(), g.getCost(), g.isRented(), false, g
            ));
        }
    }

    public void refreshData() {
        loadMasterData();
    }

    private void handleAddGear() {
        // Return a ViewModel instead of an Entity
        AddGearDialog dialog = new AddGearDialog();
        Optional<InventoryItemViewModel> result = dialog.showAndWait();

        if (result.isPresent()) {
            InventoryItemViewModel item = result.get();

            if (item.isTentEntity()) {
                // Save as Tent Entity
                Tent newTent = new Tent(item.getModel(), item.getCapacity(), item.getCost(), false);
                inventoryService.addTent(newTent);
            } else {
                // Save as Gear Entity
                Gear newGear = new Gear(item.getModel(), item.getType(), item.getCapacity(), item.getCost(), false);
                inventoryService.addGear(newGear);
            }
            loadMasterData();
        }
    }

    private void handleEditGear() {
        InventoryItemViewModel selected = gearTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            EditGearDialog dialog = new EditGearDialog(selected);
            Optional<InventoryItemViewModel> result = dialog.showAndWait();

            if (result.isPresent()) {
                InventoryItemViewModel updated = result.get();

                if (selected.isTentEntity()) {
                    Tent tent = (Tent) selected.getOriginalEntity();
                    tent.setModel(updated.getModel());
                    tent.setCapacity(updated.getCapacity());
                    tent.setCost(updated.getCost());
                    inventoryService.updateTent(tent);
                } else {
                    Gear gear = (Gear) selected.getOriginalEntity();
                    gear.setModel(updated.getModel());
                    gear.setType(updated.getType());
                    gear.setCapacity(updated.getCapacity());
                    gear.setCost(updated.getCost());
                    inventoryService.updateGear(gear);
                }
                loadMasterData();
            }
        } else {
            showSelectionError("error.pleaseSelectEditItem");
        }
    }

    private void handleRemoveGear() {
        InventoryItemViewModel selected = gearTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean confirm = UIUtil.showConfirmationAlert(
                    LanguageManager.getInstance().getString("confirm.removal"),
                    null,
                    LanguageManager.getInstance().getString("txt.delete") + selected.getModel() + "?"
            );

            if (confirm) {
                if (selected.isTentEntity()) {
                    inventoryService.deleteTent((Tent) selected.getOriginalEntity());
                } else {
                    inventoryService.deleteGear((Gear) selected.getOriginalEntity());
                }
                masterData.remove(selected);
            }
        } else {
            showSelectionError("error.pleaseSelectRemoveItem");
        }
    }

    private void showSelectionError(String contentKey) {
        UIUtil.showErrorAlert(
                LanguageManager.getInstance().getString("error.noItemSelected"),
                null,
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
        btnRefresh.setOnAction(e -> loadMasterData());

        return new HBox(10, btnAdd, btnEdit, btnRemove, btnRefresh);
    }
}