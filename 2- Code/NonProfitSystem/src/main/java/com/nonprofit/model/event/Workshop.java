package com.nonprofit.model.event;

import com.nonprofit.model.lookup.Skill;
import java.time.LocalDate;

/**
 * FACTORY METHOD PATTERN - Concrete Product: a training session.
 */
public class Workshop extends Event {

    private String trainer;
    private String topic;

    public Workshop(int id, String title, LocalDate date, String venue, int capacity,
                    double ticketPrice, Skill requiredSkill, String trainer, String topic) {
        super(id, title, date, venue, capacity, ticketPrice, requiredSkill);
        this.trainer = trainer;
        this.topic = topic;
    }

    @Override
    public String getType() {
        return "Workshop";
    }

    @Override
    public String getDetails() {
        return topic + " by " + trainer;
    }

    public String getTrainer() {
        return trainer;
    }

    public String getTopic() {
        return topic;
    }
}
