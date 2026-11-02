package com.nonprofit.model.event.ticket;

/**
 * DECORATOR PATTERN - Decorator for tickets: IS-A Ticket and HAS-A Ticket.
 */
public abstract class TicketDecorator implements Ticket {

    protected Ticket wrapped;

    public TicketDecorator(Ticket wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public abstract double getPrice();

    @Override
    public abstract String getDescription();
}
