package com.nonprofit.model.event.factory;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.event.Event;
import com.nonprofit.model.event.EventForm;
import java.time.LocalDate;

/**
 * FACTORY METHOD PATTERN - Creator for events.
 * openEvent() is the common workflow (validate, create, save);
 * createEvent() is the factory method each event type implements.
 */
public abstract class EventCreator {

    /** Returns the new event, or null if the form is invalid. */
    public Event openEvent(EventForm form) {
        if (form.getName() == null || form.getName().isBlank() || form.getDate() == null
                || form.getDate().isBefore(LocalDate.now()) || form.getCapacity() <= 0 || form.getTicketPrice() < 0) {
            return null;
        }
        Event event = createEvent(DatabaseManager.getInstance().nextId(), form);
        DatabaseManager.getInstance().getEvents().insert(event);
        return event;
    }

    /** The factory method. */
    protected abstract Event createEvent(int id, EventForm form);
}
