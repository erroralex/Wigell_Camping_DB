package com.nilsson.service;

import com.nilsson.entity.*;
import com.nilsson.exception.InvalidDateRangeException;
import com.nilsson.exception.ItemAlreadyRentedException;
import com.nilsson.exception.RentalAlreadyReturnedException;
import com.nilsson.exception.ResourceNotFoundException;
import com.nilsson.repo.*;
import com.nilsson.service.policy.PremiumPricePolicy;
import com.nilsson.service.policy.PricePolicy;
import com.nilsson.service.policy.StandardPricePolicy;
import com.nilsson.service.policy.StudentPricePolicy;
import com.nilsson.util.LanguageManager;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class RentalService {
    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;
    private final TentRepository tentRepo;
    private final GearRepository gearRepo;

    // Dependency Injection
    public RentalService(RentalRepository rentalRepo,
                         VehicleRepository vehicleRepo,
                         TentRepository tentRepo,
                         GearRepository gearRepo) {

        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.tentRepo = tentRepo;
        this.gearRepo = gearRepo;
    }

    private PricePolicy getPolicyForMember(Member member) {
        switch (member.getMembershipLevel()) {
            case STUDENT:
                return new StudentPricePolicy();
            case PREMIUM:
                return new PremiumPricePolicy();
            default:
                return new StandardPricePolicy();
        }
    }

    public void rentItem(Member member, Long itemId, RentalType type, LocalDateTime startDate) {
        switch (type) {
            case VEHICLE -> rentVehicle(member, itemId, startDate);
            case GEAR -> rentGear(member, itemId, startDate);
            case TENT -> rentTent(member, itemId, startDate);
        }
    }

    public void returnItem(Rental rental) {
        if (rental.getEndTime() != null) {
            throw new RentalAlreadyReturnedException(
                    LanguageManager.getInstance().getString("error.rentalAlreadyReturned")
            );
        }

        LocalDateTime returnDate = LocalDateTime.now();

        if (returnDate.isBefore(rental.getStartTime())) {
            throw new InvalidDateRangeException(LanguageManager.getInstance().getString("error.InvalidDateRangeException"));
        }

        rental.setEndTime(returnDate);

        BigDecimal itemCostPerDay = BigDecimal.ZERO;

        switch (rental.getRentalType()) {
            case VEHICLE -> {
                Vehicle v = vehicleRepo.getVehicle(rental.getRentalObjectId());
                if (v != null) {
                    itemCostPerDay = v.getCost();
                    v.setRented(false);
                    vehicleRepo.update(v);
                }
            }
            case GEAR -> {
                Gear g = gearRepo.getGear(rental.getRentalObjectId());
                if (g != null) {
                    itemCostPerDay = g.getCost();
                    g.setRented(false);
                    gearRepo.update(g);
                }
            }
            case TENT -> {
                Tent t = tentRepo.getTent(rental.getRentalObjectId());
                if (t != null) {
                    itemCostPerDay = t.getCost();
                    t.setRented(false);
                    tentRepo.update(t);
                }
            }
        }

        long days = Duration.between(rental.getStartTime(), rental.getEndTime()).toDays();
        if (days < 1) {
            days = 1;
        }

        PricePolicy policy = getPolicyForMember(rental.getMember());
        BigDecimal totalCost = policy.calculatePrice(itemCostPerDay, days);

        rental.setTotalCost(totalCost);

        // Update return
        rentalRepo.update(rental);
    }

    private void rentVehicle(Member member, Long vehicleId, LocalDateTime date) {
        Vehicle vehicle = vehicleRepo.getVehicle(vehicleId);
        if (vehicle == null)
            throw new ResourceNotFoundException(LanguageManager.getInstance().getString("error.vehicleNotFound"));
        if (vehicle.isRented())
            throw new ItemAlreadyRentedException(LanguageManager.getInstance().getString("error.vehicleAlreadyRented"));

        // Create rental
        Rental rental = new Rental(member, RentalType.VEHICLE, vehicle.getId(), date);

        // Update status
        vehicle.setRented(true);
        vehicleRepo.update(vehicle);

        // Save rental
        rentalRepo.save(rental);
    }

    private void rentGear(Member member, Long gearId, LocalDateTime date) {
        Gear gear = gearRepo.getGear(gearId);
        if (gear == null)
            throw new ResourceNotFoundException(LanguageManager.getInstance().getString("error.gearNotFound"));
        if (gear.isRented())
            throw new ItemAlreadyRentedException(LanguageManager.getInstance().getString("error.gearAlreadyRented"));

        // Create rental
        Rental rental = new Rental(member, RentalType.GEAR, gear.getId(), date);

        // Update status
        gear.setRented(true);
        gearRepo.update(gear);

        // Save rental
        rentalRepo.save(rental);
    }

    private void rentTent(Member member, Long tentId, LocalDateTime date) {
        Tent tent = tentRepo.getTent(tentId);
        if (tent == null)
            throw new ResourceNotFoundException(LanguageManager.getInstance().getString("error.tentNotFound"));
        if (tent.isRented())
            throw new ItemAlreadyRentedException(LanguageManager.getInstance().getString("error.tentAlreadyRented"));

        // Create rental
        Rental rental = new Rental(member, RentalType.TENT, tent.getId(), date);

        // Update status
        tent.setRented(true);
        tentRepo.update(tent);

        // Save rental
        rentalRepo.save(rental);
    }

    public List<Rental> getAllRentals() {
        return rentalRepo.getAllRentals();
    }
}