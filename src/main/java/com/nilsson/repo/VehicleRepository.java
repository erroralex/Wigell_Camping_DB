package com.nilsson.repo;

import com.nilsson.entity.Vehicle;

import java.util.List;

public interface VehicleRepository {
    Vehicle getVehicle(Long id);
    List<Vehicle> getAllVehicles();
    void save(Vehicle vehicle);
    void update(Vehicle vehicle);
    void delete(Vehicle vehicle);
}
