package com.nilsson.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "gear")
public class Gear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "capacity", nullable = false, length = 50)
    private String capacity;

    @Column(name = "cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "is_rented")
    private boolean isRented;

    // Constructors
    protected Gear() {

    }

    public Gear(String model, String type, String capacity, BigDecimal cost, boolean isRented) {
        this.model = model;
        this.type = type;
        this.capacity = capacity;
        this.cost = cost;
        this.isRented = isRented;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

}
