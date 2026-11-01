package com.nonprofit.model.event.factory;

import com.nonprofit.model.event.Event;
import com.nonprofit.model.event.EventForm;
import com.nonprofit.model.event.Outreach;

/**
 * FACTORY METHOD PATTERN - Concrete Creator for outreach programs.
 */
public class OutreachCreator extends EventCreator {

    @Override
    protected Event createEvent(int id, EventForm form) {
        return new Outreach(id, form.getName(), form.getDate(), form.getVenue(), form.getCapacity(),
                form.getTicketPrice(), form.getRequiredSkill(), form.getLocation(), form.getTeamSize());
    }
}
