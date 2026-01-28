package com.nilsson.service;

import com.nilsson.entity.*;
import com.nilsson.exception.ItemAlreadyRentedException;
import com.nilsson.exception.RentalAlreadyReturnedException;
import com.nilsson.repo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private TentRepository tentRepository;

    @Mock
    private GearRepository gearRepository;

    @InjectMocks
    private RentalService rentalService;

    @Test
    void rentVehicle_whenDetailsCorrect_shouldCallSave() {
        // Arrange
        Long vehicleId = (Long) 42L;
        Vehicle vehicle = new Vehicle(
                "Volvo",
                "Valp",
                "1978",
                "CAMPERVAN",
                "5 seats",
                BigDecimal.valueOf(500),
                false
        );
        setIdViaReflection(vehicle, vehicleId);
        Member member = new Member("Alexander", "Nilsson", MembershipLevel.STUDENT);

        when(vehicleRepository.getVehicle(vehicleId)).thenReturn(vehicle);

        // Act
        rentalService.rentItem(member, vehicleId, RentalType.VEHICLE, LocalDateTime.now());

        // Assert
        verify(vehicleRepository).getVehicle(vehicleId);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository).save(rentalCaptor.capture());

        Rental capturedRental = rentalCaptor.getValue();

        assertNotNull(capturedRental);
        assertEquals(member, capturedRental.getMember());
        assertEquals(vehicleId, capturedRental.getRentalObjectId());
        assertEquals(RentalType.VEHICLE, capturedRental.getRentalType());
        assertTrue(vehicle.isRented());

        // Verify
        verify(vehicleRepository).update(vehicle);
    }

    @Test
    void rentVehicle_whenAlreadyRented_shouldThrowException() {
        // Arrange
        Long vehicleId = (Long) 42L;
        Vehicle vehicle = new Vehicle(
                "Volvo",
                "Valp",
                "1978",
                "CAMPERVAN",
                "5 seats",
                BigDecimal.valueOf(500),
                true
        );
        setIdViaReflection(vehicle, vehicleId);
        Member member = new Member("Alexander", "Nilsson", MembershipLevel.STUDENT);

        when(vehicleRepository.getVehicle(vehicleId)).thenReturn(vehicle);

        // Act & Assert
        assertThrows(ItemAlreadyRentedException.class, () ->
                rentalService.rentItem(member, vehicleId, RentalType.VEHICLE, LocalDateTime.now())
        );

        // Verify
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void returnItem_whenRentalActive_shouldCalculatePriceAndUpdate() {
        // Arrange
        Long vehicleId = (Long) 42L;
        Vehicle vehicle = new Vehicle(
                "Volvo",
                "Valp",
                "1978",
                "CAMPERVAN",
                "5 seats",
                BigDecimal.valueOf(500),
                true
        );
        setIdViaReflection(vehicle, vehicleId);
        Member member = new Member("Alexander", "Nilsson", MembershipLevel.STANDARD);
        Rental rental = new Rental(member, RentalType.VEHICLE, vehicleId, LocalDateTime.now().minusDays(5));

        when(vehicleRepository.getVehicle(vehicleId)).thenReturn(vehicle);

        // Act
        rentalService.returnItem(rental);

        // Assert
        assertNotNull(rental.getEndTime());
        assertEquals(0, BigDecimal.valueOf(2500.0).compareTo(rental.getTotalCost()));
        assertFalse(vehicle.isRented());

        // Verify
        verify(vehicleRepository).update(vehicle);
    }

    @Test
    void returnItem_whenAlreadyReturned_shouldThrowException() {
        // Arrange
        Rental rental = new Rental(
                new Member(
                        "Alexander",
                        "Nilsson",
                        MembershipLevel.STUDENT),

                RentalType.VEHICLE,
                (Long) 42L,
                LocalDateTime.now());
        rental.setEndTime(LocalDateTime.now());

        // Act & Assert
        assertThrows(RentalAlreadyReturnedException.class, () ->
                rentalService.returnItem(rental)
        );

        // Verify
        verify(rentalRepository, never()).update(any());
    }

    private static void setIdViaReflection(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID via reflection", e);
        }
    }
}