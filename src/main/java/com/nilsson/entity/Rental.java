package com.nilsson.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Many Rentals can belong to One Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "rental_date", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "return_date")
    private LocalDateTime endTime;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    // Enum for the type
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private RentalType rentalType;

    // ID Reference to the object (Vehicle/Tent/Gear ID)
    @Column(name = "item_id", nullable = false)
    private Long rentalObjectId;

    // Constructors
    protected Rental() {}

    public Rental(Member member, RentalType rentalType, Long rentalObjectId, LocalDateTime startTime) {
        this.member = member;
        this.rentalType = rentalType;
        this.rentalObjectId = rentalObjectId;
        this.startTime = startTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public RentalType getRentalType() { return rentalType; }
    public void setRentalType(RentalType rentalType) { this.rentalType = rentalType; }
    public Long getRentalObjectId() { return rentalObjectId; }
    public void setRentalObjectId(Long rentalObjectId) { this.rentalObjectId = rentalObjectId; }
}