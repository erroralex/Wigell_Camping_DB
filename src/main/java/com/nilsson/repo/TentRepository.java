package com.nilsson.repo;

import com.nilsson.entity.Tent;

import java.util.List;

public interface TentRepository {
    Tent getTent(Long id);
    List<Tent> getAllTents();
    void addTent(Tent tent);
    void updateTent(Tent tent);
    void deleteTent(Tent tent);
    List<Tent> findByIsRentedFalse();
}
