package com.nilsson.repo;

import com.nilsson.entity.Gear;

import java.util.List;

public interface GearRepository {
    Gear getGear(Long id);
    List<Gear> getAllGear();
    void addGear(Gear gear);
    void updateGear(Gear gear);
    void deleteGear(Gear gear);
    List<Gear> findByIsRentedFalse();
}
