package com.nonprofit.model.event.ticket;

import com.nonprofit.model.event.Event;
import com.nonprofit.model.people.Person;

/**
 * DECORATOR PATTERN - Concrete Component: a standard entry ticket.
 */
public class BasicTicket implements Ticket {

    private double basePrice;
    private Event event;
    private Person holder;

    public BasicTicket(double basePrice, Event event, Person holder) {
        this.basePrice = basePrice;
        this.event = event;
        this.holder = holder;
    }

    @Override
    public double getPrice() {
        return basePrice;
    }

    @Override
    public String getDescription() {
        return "Entry to '" + event.getName() + "' for " + holder.getName();
    }

    public Event getEvent() {
        return event;
    }

    public Person getHolder() {
        return holder;
    }
}
