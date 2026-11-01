package com.nonprofit.model.event;

import com.nonprofit.model.lookup.Skill;
import java.time.LocalDate;

/**
 * The data staff fill in for a new event. Common fields come in the
 * constructor; each event type reads its own extra fields.
 */
public class EventForm {

    private String name;
    private LocalDate date;
    private String venue;
    private int capacity;
    private double ticketPrice;
    private Skill requiredSkill;
    private double targetAmount;
    private String trainer;
    private String topic;
    private String location;
    private int teamSize;

    public EventForm(String name, LocalDate date, String venue, int capacity,
                     double ticketPrice, Skill requiredSkill) {
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.capacity = capacity;
        this.ticketPrice = ticketPrice;
        this.requiredSkill = requiredSkill;
    }

    public void setFundraiserDetails(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setWorkshopDetails(String trainer, String topic) {
        this.trainer = trainer;
        this.topic = topic;
    }

    public void setOutreachDetails(String location, int teamSize) {
        this.location = location;
        this.teamSize = teamSize;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getVenue() {
        return venue;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public Skill getRequiredSkill() {
        return requiredSkill;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public String getTrainer() {
        return trainer;
    }

    public String getTopic() {
        return topic;
    }

    public String getLocation() {
        return location;
    }

    public int getTeamSize() {
        return teamSize;
    }
}
