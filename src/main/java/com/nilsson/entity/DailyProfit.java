package com.nilsson.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "profits")
public class DailyProfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private final LocalDate date;

    @Column(name = "amount", precision = 10, scale = 2)
    private final BigDecimal income;

    public DailyProfit(LocalDate date, BigDecimal income) {
        this.date = date;
        this.income = income;
    }

    public LocalDate getDate() { return date; }
    public BigDecimal getIncome() { return income; }
}