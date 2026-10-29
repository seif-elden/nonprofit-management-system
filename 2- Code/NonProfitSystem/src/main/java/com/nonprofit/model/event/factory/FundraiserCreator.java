package com.nonprofit.model.event.factory;

import com.nonprofit.model.event.Event;
import com.nonprofit.model.event.EventForm;
import com.nonprofit.model.event.Fundraiser;

/**
 * FACTORY METHOD PATTERN - Concrete Creator for fundraisers.
 */
public class FundraiserCreator extends EventCreator {

    @Override
    protected Event createEvent(int id, EventForm form) {
        return new Fundraiser(id, form.getName(), form.getDate(), form.getVenue(), form.getCapacity(),
                form.getTicketPrice(), form.getRequiredSkill(), form.getTargetAmount());
    }
}
