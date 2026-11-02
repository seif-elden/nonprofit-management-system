package com.nonprofit.model.volunteer;

import com.nonprofit.model.common.Identifiable;
import com.nonprofit.model.event.Event;
import com.nonprofit.model.people.Volunteer;
import java.time.LocalDate;

/**
 * A volunteer working at an event in a given role.
 */
public class Assignment implements Identifiable {

    private int id;
    private Volunteer volunteer;
    private Event event;
    private String role;
    private double hoursWorked;
    private LocalDate date;

    public Assignment(int id, Volunteer volunteer, Event event, String role) {
        this.id = id;
        this.volunteer = volunteer;
        this.event = event;
        this.role = role;
        this.hoursWorked = 0;
        this.date = event.getDate();
    }

    /** Adds hours to this assignment and to the volunteer's total. */
    public void logHours(double hours) {
        hoursWorked += hours;
        volunteer.addHours(hours);
    }

    @Override
    public int getId() {
        return id;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public Event getEvent() {
        return event;
    }

    public String getRole() {
        return role;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public LocalDate getDate() {
        return date;
    }
}
