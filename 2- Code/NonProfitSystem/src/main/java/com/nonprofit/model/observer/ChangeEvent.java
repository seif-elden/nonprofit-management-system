package com.nonprofit.model.observer;

import java.time.LocalDateTime;

/**
 * OBSERVER PATTERN - the data pushed to observers: what changed (type),
 * the changed object (payload) and a readable description.
 */
public class ChangeEvent {

    private ChangeType type;
    private Object payload;
    private String description;
    private LocalDateTime timestamp;

    public ChangeEvent(ChangeType type, Object payload, String description) {
        this.type = type;
        this.payload = payload;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public ChangeType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
