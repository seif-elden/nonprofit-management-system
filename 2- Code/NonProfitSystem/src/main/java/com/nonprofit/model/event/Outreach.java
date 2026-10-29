package com.nonprofit.model.event;

import com.nonprofit.model.lookup.Skill;
import java.time.LocalDate;

/**
 * FACTORY METHOD PATTERN - Concrete Product: a field visit / aid convoy.
 */
public class Outreach extends Event {

    private String location;
    private int teamSize;

    public Outreach(int id, String title, LocalDate date, String venue, int capacity,
                    double ticketPrice, Skill requiredSkill, String location, int teamSize) {
        super(id, title, date, venue, capacity, ticketPrice, requiredSkill);
        this.location = location;
        this.teamSize = teamSize;
    }

    @Override
    public String getType() {
        return "Outreach";
    }

    @Override
    public String getDetails() {
        return location + ", team of " + teamSize;
    }

    public String getLocation() {
        return location;
    }

    public int getTeamSize() {
        return teamSize;
    }
}
