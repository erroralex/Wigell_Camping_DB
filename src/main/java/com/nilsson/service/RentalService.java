package com.nilsson.service;

import com.nilsson.entity.*;
import com.nilsson.exception.ItemAlreadyRentedException;
import com.nilsson.exception.ResourceNotFoundException;
import com.nilsson.repo.*;
import com.nilsson.util.LanguageManager;

import java.time.LocalDateTime;

public class RentalService {
    private final RentalRepositoryImpl rentalRepo;
    private final VehicleRepositoryImpl vehicleRepo;
    private final TentRepositoryImpl tentRepo;
    private final GearRepositoryImpl gearRepo;

    // Dependency Injection via konstruktor (KRAV)
    public RentalService(RentalRepositoryImpl rentalRepo,
                         VehicleRepositoryImpl vehicleRepo,
                         TentRepositoryImpl tentRepo,
                         GearRepositoryImpl gearRepo) {

        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.tentRepo = tentRepo;
        this.gearRepo = gearRepo;
    }

    // En gemensam metod eller separata metoder beroende på designval
    public void rentItem(Member member, Long itemId, RentalType type) {
        // Validering och statusuppdatering måste ske per typ
        switch (type) {
            case VEHICLE -> rentVehicle(member, itemId);
            case GEAR    -> rentGear(member, itemId);
            case TENT    -> rentTent(member, itemId);
        }
    }

    private void rentVehicle(Member member, Long vehicleId) {
        Vehicle v = vehicleRepo.getVehicle(vehicleId);
        if (v == null) throw new ResourceNotFoundException(LanguageManager.getInstance().getString("error.vehicleNotFound"));
        if (v.isRented()) throw new ItemAlreadyRentedException(LanguageManager.getInstance().getString("error.vehicleAlreadyRented"));

        // Skapa uthyrningen
        Rental rental = new Rental(member, RentalType.VEHICLE, v.getId(), LocalDateTime.now());

        // Create rental
        v.setRented(true);
        vehicleRepo.updateVehicle(v);

        // Save rental
        rentalRepo.save(rental);
    }

    private void rentGear(Member member, Long gearId) {
        Gear g = gearRepo.getGear(gearId);
        if (g == null) throw new ResourceNotFoundException(LanguageManager.getInstance().getString("error.gearNotFound"));
        if (g.isRented()) throw new ItemAlreadyRentedException(LanguageManager.getInstance().getString("error.gearAlreadyRented"));

        // Create rental
        Rental rental = new Rental(member, RentalType.GEAR, g.getId(), LocalDateTime.now());

        // Update status
        g.setRented(true);
        gearRepo.updateGear(g);

        // Save rental
        rentalRepo.save(rental);
    }

    private void rentTent(Member member, Long tentId) {
        Tent t = tentRepo.getTent(tentId);
        if (t == null) throw new ResourceNotFoundException(LanguageManager.getInstance().getString("error.tentNotFound"));
        if (t.isRented()) throw new ItemAlreadyRentedException(LanguageManager.getInstance().getString("error.tentAlreadyRented"));

        // Create rental
        Rental rental = new Rental(member, RentalType.TENT, t.getId(), LocalDateTime.now());

        // Update status
        t.setRented(true);
        tentRepo.updateTent(t);

        // Save rental
        rentalRepo.save(rental);
    }
}