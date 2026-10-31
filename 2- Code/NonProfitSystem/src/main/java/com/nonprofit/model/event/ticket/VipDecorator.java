package com.nonprofit.model.event.ticket;

/**
 * DECORATOR PATTERN - Concrete Decorator: VIP seating.
 */
public class VipDecorator extends TicketDecorator {

    private static final double VIP_PRICE = 250;

    public VipDecorator(Ticket wrapped) {
        super(wrapped);
    }

    @Override
    public double getPrice() {
        return wrapped.getPrice() + VIP_PRICE;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + ", VIP seating";
    }
}
