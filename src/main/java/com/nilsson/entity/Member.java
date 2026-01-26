package com.nilsson.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 50)
    private MembershipLevel membershipLevel;

    @Column(name = "entered_date")
    private LocalDate enteredDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_history", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "event_description", length = 255)
    private List<String> history = new ArrayList<>();

    // Constructors
    protected Member() {
        this.enteredDate = LocalDate.now();
    }

    public Member(String firstName, String lastName, MembershipLevel membershipLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.membershipLevel = membershipLevel;
        this.enteredDate = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public MembershipLevel getMembershipLevel() { return membershipLevel; }
    public void setMembershipLevel(MembershipLevel membershipLevel) { this.membershipLevel = membershipLevel; }

    public LocalDate getEnteredDate() { return enteredDate; }
    public void setEnteredDate(LocalDate enteredDate) { this.enteredDate = enteredDate; }

    public List<String> getHistory() { return history; }
    public void setHistory(List<String> history) { this.history = history; }

    public void addHistoryEvent(String event) {
        this.history.add(event);
    }
}