package com.nilsson.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "make", nullable = false, length = 50)
    private String make;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "`year`", nullable = false, length = 10)
    private String year;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "capacity", nullable = false, length = 50)
    private String capacity;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "is_rented")
    private boolean isRented;

    // Constructors
    protected Vehicle() {}

    public Vehicle(String make, String model, String year, String type, String capacity, BigDecimal cost, boolean isRented) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
        this.capacity = capacity;
        this.cost = cost;
        this.isRented = isRented;

    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }
}