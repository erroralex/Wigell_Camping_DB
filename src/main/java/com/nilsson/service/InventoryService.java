package com.nilsson.service;

import com.nilsson.entity.Gear;
import com.nilsson.entity.Tent;
import com.nilsson.entity.Vehicle;
import com.nilsson.exception.ItemActiveException;
import com.nilsson.repo.*;
import com.nilsson.util.LanguageManager;

import java.util.List;

public class InventoryService {

    private final GearRepository gearRepo;
    private final TentRepository tentRepo;
    private final VehicleRepository vehicleRepo;

    public InventoryService(GearRepository gearRepo,
                            TentRepository tentRepo,
                            VehicleRepository vehicleRepo) {

        this.gearRepo = gearRepo;
        this.tentRepo = tentRepo;
        this.vehicleRepo = vehicleRepo;
    }

    // ────────────────────── GEAR OPERATIONS ──────────────────────

    public List<Gear> getAllGear() {
        return gearRepo.getAllGear();
    }

    public Gear getGear(Long id) {
        return gearRepo.getGear(id);
    }

    public void saveGear(Gear gear) {
        gearRepo.save(gear);
    }

    public void updateGear(Gear gear) {
        gearRepo.update(gear);
    }

    public void deleteGear(Gear gear) {
        if (gear.isRented()) {
            throw new ItemActiveException(LanguageManager.getInstance().getString("error.GearActiveExceptionPartOne")
                    + gear.getModel()
                    + LanguageManager.getInstance().getString("error.ItemActiveExceptionPartTwo"));
        }
        gearRepo.delete(gear);
    }

    // ────────────────────── TENT OPERATIONS ──────────────────────

    public List<Tent> getAllTents() {
        return tentRepo.getAllTents();
    }

    public Tent getTent(Long id) {
        return tentRepo.getTent(id);
    }

    public void saveTent(Tent tent) {
        tentRepo.save(tent);
    }

    public void updateTent(Tent tent) {
        tentRepo.update(tent);
    }

    public void deleteTent(Tent tent) {
        if (tent.isRented()) {
            throw new ItemActiveException(LanguageManager.getInstance().getString("error.TentActiveExceptionPartOne")
                    + tent.getModel()
                    + LanguageManager.getInstance().getString("error.ItemActiveExceptionPartTwo"));
        }
        tentRepo.delete(tent);
    }

    // ────────────────────── VEHICLE OPERATIONS ──────────────────────

    public List<Vehicle> getAllVehicles() {
        return vehicleRepo.getAllVehicles();
    }

    public Vehicle getVehicle(Long id) {
        return vehicleRepo.getVehicle(id);
    }

    public void saveVehicle(Vehicle vehicle) {
        vehicleRepo.save(vehicle);
    }

    public void updateVehicle(Vehicle vehicle) {
        vehicleRepo.update(vehicle);
    }

    public void deleteVehicle(Vehicle vehicle) {
        if (vehicle.isRented()) {
            throw new ItemActiveException(LanguageManager.getInstance().getString("error.VehicleActiveExceptionPartOne")
                    + vehicle.getModel()
                    + LanguageManager.getInstance().getString("error.ItemActiveExceptionPartTwo"));
        }
        vehicleRepo.delete(vehicle);
    }
}