package com.nilsson.service;

import com.nilsson.entity.Gear;
import com.nilsson.entity.RentalType;
import com.nilsson.exception.ItemActiveException;
import com.nilsson.repo.GearRepository;
import com.nilsson.repo.TentRepository;
import com.nilsson.repo.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private GearRepository gearRepository;

    @Mock
    private TentRepository tentRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void deleteGear_whenGearIsRented_shouldThrowException() {
        // Arrange
        Gear gear = new Gear(
                "Kayak",
                RentalType.GEAR.toString(),
                "1 person",
                BigDecimal.valueOf(100),
                true
        );

        // Act & Assert
        assertThrows(ItemActiveException.class, () -> inventoryService.deleteGear(gear));

        // Verify
        verify(gearRepository, never()).delete(any());
    }

    @Test
    void deleteGear_whenNotRented_shouldCallDelete() {
        // Arrange
        Gear gear = new Gear(
                "Kayak",
                RentalType.GEAR.toString(),
                "1 person",
                BigDecimal.valueOf(100),
                false
        );

        // Act
        inventoryService.deleteGear(gear);

        // Assert & Verify
        verify(gearRepository).delete(gear);
    }
}