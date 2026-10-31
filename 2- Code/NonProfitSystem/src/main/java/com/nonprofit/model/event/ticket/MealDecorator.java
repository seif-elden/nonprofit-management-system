package com.nonprofit.model.event.ticket;

/**
 * DECORATOR PATTERN - Concrete Decorator: dinner included.
 */
public class MealDecorator extends TicketDecorator {

    private static final double MEAL_PRICE = 150;

    public MealDecorator(Ticket wrapped) {
        super(wrapped);
    }

    @Override
    public double getPrice() {
        return wrapped.getPrice() + MEAL_PRICE;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + ", dinner";
    }
}
