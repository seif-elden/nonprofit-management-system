package com.nonprofit.model.observer;

/** The kinds of change a subject can publish. */
public enum ChangeType {
    DONATION_RECORDED,
    EVENT_RESCHEDULED,
    EVENT_VENUE_CHANGED,
    EVENT_CANCELLED,
    EVENT_REMINDER,
    EVENT_COMPLETED,
    DISTRIBUTION_RECORDED
}
