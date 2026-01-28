package com.nilsson.repo;

import com.nilsson.entity.Gear;

import java.util.List;

public interface GearRepository {
    Gear getGear(Long id);
    List<Gear> getAllGear();
    void save(Gear gear);
    void update(Gear gear);
    void delete(Gear gear);
}
