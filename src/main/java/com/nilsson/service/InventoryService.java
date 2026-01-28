package com.nilsson.service;

import com.nilsson.entity.Gear;
import com.nilsson.entity.Tent;
import com.nilsson.entity.Vehicle;
import com.nilsson.exception.ItemActiveException;
import com.nilsson.repo.*;
import com.nilsson.util.LanguageManager;

import java.util.List;

public class InventoryService {

    private final GearRepository gearRepository;
    private final TentRepository tentRepository;
    private final VehicleRepository vehicleRepository;

    public InventoryService(GearRepository gearRepository,
                            TentRepository tentRepository,
                            VehicleRepository vehicleRepository) {

        this.gearRepository = gearRepository;
        this.tentRepository = tentRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // ────────────────────── GEAR OPERATIONS ──────────────────────

    public List<Gear> getAllGear() {
        return gearRepository.getAllGear();
    }

    public Gear getGear(Long id) {
        return gearRepository.getGear(id);
    }

    public void addGear(Gear gear) {
        gearRepository.addGear(gear);
    }

    public void updateGear(Gear gear) {
        gearRepository.updateGear(gear);
    }

    public void deleteGear(Gear gear) {
        if (gear.isRented()) {
            throw new ItemActiveException(LanguageManager.getInstance().getString("error.GearActiveExceptionPartOne")
                    + gear.getModel()
                    + LanguageManager.getInstance().getString("error.ItemActiveExceptionPartTwo"));
        }
        gearRepository.deleteGear(gear);
    }

    // ────────────────────── TENT OPERATIONS ──────────────────────

    public List<Tent> getAllTents() {
        return tentRepository.getAllTents();
    }

    public Tent getTent(Long id) {
        return tentRepository.getTent(id);
    }

    public void addTent(Tent tent) {
        tentRepository.addTent(tent);
    }

    public void updateTent(Tent tent) {
        tentRepository.updateTent(tent);
    }

    public void deleteTent(Tent tent) {
        if (tent.isRented()) {
            throw new ItemActiveException(LanguageManager.getInstance().getString("error.TentActiveExceptionPartOne")
                    + tent.getModel()
                    + LanguageManager.getInstance().getString("error.ItemActiveExceptionPartTwo"));
        }
        tentRepository.deleteTent(tent);
    }

    // ────────────────────── VEHICLE OPERATIONS ──────────────────────

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.getAllVehicles();
    }

    public Vehicle getVehicle(Long id) {
        return vehicleRepository.getVehicle(id);
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.addVehicle(vehicle);
    }

    public void updateVehicle(Vehicle vehicle) {
        vehicleRepository.updateVehicle(vehicle);
    }

    public void deleteVehicle(Vehicle vehicle) {
        if (vehicle.isRented()) {
            throw new ItemActiveException(LanguageManager.getInstance().getString("error.VehicleActiveExceptionPartOne")
                    + vehicle.getModel()
                    + LanguageManager.getInstance().getString("error.ItemActiveExceptionPartTwo"));
        }
        vehicleRepository.deleteVehicle(vehicle);
    }
}