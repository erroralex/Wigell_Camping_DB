package com.nilsson.model;

import com.nilsson.entity.*;
import com.nilsson.exception.UnknownItemTypeException;

import java.time.LocalDateTime;

public class NewRentalResult {
    private final Member member;
    private final RentalType type;
    private final Long objectId;
    private final LocalDateTime startTime;
    private final Object item;

    public NewRentalResult(Member member, Object item, RentalType type, LocalDateTime startTime) {
        this.member = member;
        this.type = type;
        this.startTime = startTime;
        this.item = item;

        // Extraction of ID
        if (item instanceof Vehicle v) {
            this.objectId = v.getId();
        } else if (item instanceof Gear g) {
            this.objectId = g.getId();
        } else if (item instanceof Tent t) {
            this.objectId = t.getId();
        } else {
            throw new UnknownItemTypeException("Unknown item type");
        }
    }

    public Member getMember() { return member; }
    public RentalType getType() { return type; }
    public Long getObjectId() { return objectId; }
    public LocalDateTime getStartTime() { return startTime; }

    public Object getSelectedItem() {
        return item;
    }
}