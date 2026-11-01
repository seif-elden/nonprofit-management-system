package com.nonprofit.model.event;

import com.nonprofit.model.lookup.Skill;
import java.time.LocalDate;

/**
 * FACTORY METHOD PATTERN - Concrete Product: an event that raises money.
 */
public class Fundraiser extends Event {

    private double targetAmount;

    public Fundraiser(int id, String title, LocalDate date, String venue, int capacity,
                      double ticketPrice, Skill requiredSkill, double targetAmount) {
        super(id, title, date, venue, capacity, ticketPrice, requiredSkill);
        this.targetAmount = targetAmount;
    }

    @Override
    public String getType() {
        return "Fundraiser";
    }

    @Override
    public String getDetails() {
        return String.format("target %.0f EGP", targetAmount);
    }

    public double getTargetAmount() {
        return targetAmount;
    }
}
