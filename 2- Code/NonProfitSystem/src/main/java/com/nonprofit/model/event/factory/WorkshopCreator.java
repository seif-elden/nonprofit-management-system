package com.nonprofit.model.event.factory;

import com.nonprofit.model.event.Event;
import com.nonprofit.model.event.EventForm;
import com.nonprofit.model.event.Workshop;

/**
 * FACTORY METHOD PATTERN - Concrete Creator for workshops.
 */
public class WorkshopCreator extends EventCreator {

    @Override
    protected Event createEvent(int id, EventForm form) {
        return new Workshop(id, form.getName(), form.getDate(), form.getVenue(), form.getCapacity(),
                form.getTicketPrice(), form.getRequiredSkill(), form.getTrainer(), form.getTopic());
    }
}
