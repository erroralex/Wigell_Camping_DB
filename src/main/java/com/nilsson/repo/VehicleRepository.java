package com.nilsson.repo;

import com.nilsson.entity.Vehicle;

import java.util.List;

public interface VehicleRepository {
    Vehicle getVehicle(Long id);
    List<Vehicle> getAllVehicles();
    void addVehicle(Vehicle vehicle);
    void updateVehicle(Vehicle vehicle);
    void deleteVehicle(Vehicle vehicle);
}
