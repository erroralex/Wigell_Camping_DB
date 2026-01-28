package com.nilsson.repo;

import com.nilsson.entity.*;
import com.nilsson.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RentalRepositoryImplIntegrationTest {

    private static SessionFactory sessionFactory;

    private RentalRepository rentalRepository;
    private MemberRepository memberRepository;
    private VehicleRepository vehicleRepository;

    @BeforeAll
    static void beforeAll() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @AfterAll
    static void afterAll() {
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        rentalRepository = new RentalRepositoryImpl(sessionFactory);
        memberRepository = new MemberRepositoryImpl(sessionFactory);
        vehicleRepository = new VehicleRepositoryImpl(sessionFactory);
    }

    @Test
    void save_and_find_persists_data_correctly() {
        // Arrange
        Member member = new Member("Alexander", "Nilsson", MembershipLevel.STANDARD);
        memberRepository.addMember(member);

        Vehicle vehicle = new Vehicle(
                "Volvo",
                "Valp",
                "1978",
                "CAMPERVAN",
                "5 seats",
                BigDecimal.valueOf(500.0),
                false);

        vehicleRepository.addVehicle(vehicle);

        Rental rental = new Rental(member, RentalType.VEHICLE, vehicle.getId(), LocalDateTime.now());

        // Act
        rentalRepository.save(rental);

        // Assert
        List<Rental> rentals = rentalRepository.getAllRentals();

        assertFalse(rentals.isEmpty(), "Rental list should not be empty");

        Rental savedRental = rentals.stream()
                .filter(r -> r.getMember().getId().equals(member.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(savedRental, "Should find the saved rental");
        assertNotNull(savedRental.getId(), "Saved rental should have an ID");
        assertEquals(vehicle.getId(), savedRental.getRentalObjectId(), "Rental Object ID should match");
    }

    @Test
    void update_changes_rental_status_in_db() {
        // Arrange
        Member member = new Member("Alexander", "Nilsson", MembershipLevel.STUDENT);
        memberRepository.addMember(member);

        Vehicle vehicle = new Vehicle(
                "Volvo",
                "TP21",
                "1958",
                "CAMPERVAN",
                "4 seats",
                BigDecimal.valueOf(700.0),
                false
        );
        vehicleRepository.addVehicle(vehicle);

        Rental rental = new Rental(member,RentalType.VEHICLE, vehicle.getId(), LocalDateTime.now().minusDays(1));
        rentalRepository.save(rental);

        // Act
        LocalDateTime returntime = LocalDateTime.now();
        BigDecimal cost = BigDecimal.valueOf(700);

        rental.setEndTime(returntime);
        rental.setTotalCost(cost);

        rentalRepository.update(rental);

        // Assert
        List<Rental> dbRentals = rentalRepository.getAllRentals();
        Rental updatedRental = dbRentals.stream()
                .filter(r -> r.getId().equals(rental.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(0, cost.compareTo(updatedRental.getTotalCost()), "Cost should be updated");

        long diff = ChronoUnit.MILLIS.between(updatedRental.getEndTime(), returntime);
        assertTrue(Math.abs(diff) < 500, "End time in DB should be within 500ms of expected time. Diff: " + diff + "ms");
    }
}