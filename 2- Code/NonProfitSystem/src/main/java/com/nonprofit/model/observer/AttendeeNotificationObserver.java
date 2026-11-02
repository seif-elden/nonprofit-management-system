package com.nonprofit.model.observer;

import com.nonprofit.model.event.Event;
import com.nonprofit.model.notification.Message;
import com.nonprofit.model.notification.NotificationService;
import com.nonprofit.model.people.Person;
import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER PATTERN - Concrete Observer: tells every attendee and every
 * assigned volunteer about a change to their event.
 */
public class AttendeeNotificationObserver implements Observer {

    private NotificationService notificationService;
    private int lastReached;

    public AttendeeNotificationObserver(Subject eventService, NotificationService notificationService) {
        this.notificationService = notificationService;
        eventService.addObserver(this);
    }

    @Override
    public void update(Subject source, ChangeEvent change) {
        if (!(change.getPayload() instanceof Event)) {
            return;
        }
        Event event = (Event) change.getPayload();
        List<Person> recipients = new ArrayList<>(event.getAttendees());
        for (Person volunteer : event.getVolunteers()) {
            if (!recipients.contains(volunteer)) {
                recipients.add(volunteer);
            }
        }
        lastReached = notificationService.broadcast(recipients, new Message("Update: " + event.getName(), change.getDescription()));
    }

    /** How many people the last update reached. */
    public int getLastReached() {
        return lastReached;
    }
}
