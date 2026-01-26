package com.nilsson.ui.views;

import java.math.BigDecimal;

/**
 * A wrapper class that allows the UI to display both Gear and Tents
 * in the same table, even though they are separate entities.
 */
public class InventoryItemViewModel {
    private Long id;
    private String model;
    private String type;
    private String capacity;
    private BigDecimal cost;
    private boolean isRented;

    private boolean isTentEntity;
    private Object originalEntity;

    public InventoryItemViewModel(
            Long id,
            String model,
            String type,
            String capacity,
            BigDecimal cost,
            boolean isRented,
            boolean isTentEntity,
            Object originalEntity) {

        this.id = id;
        this.model = model;
        this.type = type;
        this.capacity = capacity;
        this.cost = cost;
        this.isRented = isRented;
        this.isTentEntity = isTentEntity;
        this.originalEntity = originalEntity;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getType() {
        return type;
    }

    public String getCapacity() {
        return capacity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public boolean isRented() {
        return isRented;
    }

    public boolean isTentEntity() {
        return isTentEntity;
    }

    public Object getOriginalEntity() {
        return originalEntity;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

}