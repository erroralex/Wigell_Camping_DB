package com.nilsson.service;

import com.nilsson.jdbcApp.app.LanguageManager;
import com.nilsson.jdbcApp.model.items.Gear;
import com.nilsson.jdbcApp.model.items.IRentable;
import com.nilsson.jdbcApp.model.items.RecreationalVehicle;
import com.nilsson.jdbcApp.model.registries.Inventory;
import com.nilsson.jdbcApp.ui.UIUtil;
import com.nilsson.jdbcApp.ui.dialogs.AddGearDialog;
import com.nilsson.jdbcApp.ui.dialogs.AddVehicleDialog;
import com.nilsson.jdbcApp.ui.dialogs.EditGearDialog;
import com.nilsson.jdbcApp.ui.dialogs.EditVehicleDialog;

import java.util.List;
import java.util.Optional;

public class InventoryService {

    // ──────────────────────────────────────────────────────
    //                  Vehicle Operations
    // ──────────────────────────────────────────────────────
    public RecreationalVehicle handleAddRecreationalVehicle() {
        AddVehicleDialog dialog = new AddVehicleDialog();
        Optional<RecreationalVehicle> result = dialog.showAndWait();

        if (result.isPresent()) {
            RecreationalVehicle newVehicleData = result.get();
            // Registry calls DAO internally
            Inventory.getInstance().addRecreationalVehicle(newVehicleData);

            UIUtil.showInfoAlert(
                    LanguageManager.getInstance().getString("msg.vehicleAdded"),
                    LanguageManager.getInstance().getString("msg.success"),
                    newVehicleData.getMake() + " " + newVehicleData.getModel() +
                            LanguageManager.getInstance().getString("msg.addedSuccess"));
            return newVehicleData;
        }
        return null;
    }

    public void handleEditRecreationalVehicle(RecreationalVehicle selectedRecreationalVehicle) {
        if (selectedRecreationalVehicle == null) {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.editError"),
                    LanguageManager.getInstance().getString("error.noItemSelected"),
                    LanguageManager.getInstance().getString("error.pleaseSelectEditItem"));
            return;
        }

        EditVehicleDialog dialog = new EditVehicleDialog(selectedRecreationalVehicle);
        Optional<RecreationalVehicle> result = dialog.showAndWait();

        if (result.isPresent()) {
            RecreationalVehicle updatedRV = result.get();

            // Call update on Registry -> DAO
            Inventory.getInstance().updateRecreationalVehicle(updatedRV);

            UIUtil.showInfoAlert(
                    LanguageManager.getInstance().getString("msg.vehicleUpdated"),
                    LanguageManager.getInstance().getString("msg.success"),
                    updatedRV.getModel() + " " + updatedRV.getMake() +
                            LanguageManager.getInstance().getString("msg.updateSuccess"));
        }
    }

    public boolean handleRemoveRecreationalVehicle(RecreationalVehicle selectedRecreationalVehicle) {
        if (selectedRecreationalVehicle == null) return false;

        boolean removed = Inventory.getInstance().removeRecreationalVehicle(selectedRecreationalVehicle);

        if (removed) {
            UIUtil.showInfoAlert(
                    LanguageManager.getInstance().getString("msg.vehicleRemoved"),
                    LanguageManager.getInstance().getString("msg.success"),
                    selectedRecreationalVehicle.getMake() + " " + selectedRecreationalVehicle.getModel() +
                            LanguageManager.getInstance().getString("msg.removedSuccess"));
        }
        return removed;
    }

    // ──────────────────────────────────────────────────────
    //                  Gear Operations
    // ──────────────────────────────────────────────────────

    public Gear handleAddGear() {
        AddGearDialog dialog = new AddGearDialog();
        Optional<Gear> result = dialog.showAndWait();

        if (result.isPresent()) {
            Gear newGearData = result.get();
            Inventory.getInstance().addGear(newGearData);

            UIUtil.showInfoAlert(
                    LanguageManager.getInstance().getString("msg.gearAdded"),
                    LanguageManager.getInstance().getString("msg.success"),
                    newGearData.getModel() + LanguageManager.getInstance().getString("msg.addedSuccess"));
            return newGearData;
        }
        return null;
    }

    public void handleEditGear(Gear selectedGear) {
        if (selectedGear == null) {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.editError"),
                    LanguageManager.getInstance().getString("error.noItemSelected"),
                    LanguageManager.getInstance().getString("error.pleaseSelectEditItem"));
            return;
        }

        EditGearDialog dialog = new EditGearDialog(selectedGear);
        Optional<Gear> result = dialog.showAndWait();

        if (result.isPresent()) {
            Gear updatedGear = result.get();
            Inventory.getInstance().updateGear(updatedGear);

            UIUtil.showInfoAlert(
                    LanguageManager.getInstance().getString("msg.gearAdded"),
                    LanguageManager.getInstance().getString("msg.success"),
                    updatedGear.getModel() + LanguageManager.getInstance().getString("msg.updateSuccess"));
        }
    }

    public boolean handleRemoveGear(Gear selectedGear) {
        if (selectedGear == null) return false;

        boolean wasRemoved = Inventory.getInstance().removeGear(selectedGear);

        if (wasRemoved) {
            UIUtil.showInfoAlert(
                    LanguageManager.getInstance().getString("msg.gearRemoved"),
                    LanguageManager.getInstance().getString("msg.success"),
                    selectedGear.getModel() + LanguageManager.getInstance().getString("msg.removedSuccess"));
        } else {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.removalFailed"),
                    LanguageManager.getInstance().getString("error.registryMismatch"),
                    LanguageManager.getInstance().getString("error.couldNotFindGear"));
        }
        return wasRemoved;
    }

    public IRentable findRentableItem(String itemType, String itemName) {
        Inventory inventory = Inventory.getInstance();
        List<RecreationalVehicle> vehicles = inventory.getAvailableRecreationalVehicleList();
        for (RecreationalVehicle vehicle : vehicles) {
            if (vehicle.getItemType().equals(itemType) && vehicle.getItemName().equals(itemName)) {
                return vehicle;
            }
        }
        List<Gear> gearItems = inventory.getAvailableGearList();
        for (Gear gear : gearItems) {
            if (gear.getItemType().equals(itemType) && gear.getItemName().equals(itemName)) {
                return gear;
            }
        }
        return null;
    }

    // saveAllInventory is no longer needed as updates are immediate via DAO
}