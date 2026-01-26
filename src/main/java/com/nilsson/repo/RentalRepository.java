package com.nilsson.repo;

import com.nilsson.entity.Rental;
import java.util.List;

public interface RentalRepository {
    void save(Rental rental);
    void update(Rental rental);
    List<Rental> getRentalsByMemberId(Long memberId);
    List<Rental> getAllRentals();
}