package com.nonprofit.model.event.ticket;

/**
 * DECORATOR PATTERN - Component for event tickets.
 */
public interface Ticket {

    double getPrice();

    String getDescription();
}
