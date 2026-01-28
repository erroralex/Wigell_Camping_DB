package com.nilsson.repo;

import com.nilsson.entity.Tent;

import java.util.List;

public interface TentRepository {
    Tent getTent(Long id);
    List<Tent> getAllTents();
    void save(Tent tent);
    void update(Tent tent);
    void delete(Tent tent);
}
