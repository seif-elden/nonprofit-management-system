package com.nonprofit.model.event;

import com.nonprofit.model.event.factory.EventCreator;
import com.nonprofit.model.observer.ChangeEvent;
import com.nonprofit.model.observer.ChangeType;
import com.nonprofit.model.observer.Observer;
import com.nonprofit.model.observer.Subject;
import com.nonprofit.model.people.Person;
import com.nonprofit.model.event.ticket.BasicTicket;
import com.nonprofit.model.event.ticket.MealDecorator;
import com.nonprofit.model.event.ticket.Ticket;
import com.nonprofit.model.event.ticket.VipDecorator;
import com.nonprofit.model.volunteer.Assignment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Event business logic.
 * OBSERVER PATTERN - Concrete Subject: every change to an event is published
 * so attendees and volunteers are told automatically.
 */
public class EventService implements Subject {

    private List<Observer> observers;

    public EventService() {
        observers = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(ChangeEvent change) {
        for (Observer observer : observers) {
            observer.update(this, change);
        }
    }

    /** Factory Method: the chosen creator decides which kind of event is built. */
    public Event createEvent(EventCreator creator, EventForm form) {
        return creator.openEvent(form);
    }

    public boolean registerAttendee(Event event, Person person) {
        return event.register(person);
    }

    public boolean markAttendance(Event event, Person person) {
        return event.markAttendance(person);
    }

    /** Registers the person and builds the ticket with the Decorator pattern; extras: "VIP", "MEAL". */
    public Ticket sellTicket(Event event, Person person, List<String> extras) {
        if (!event.getAttendees().contains(person) && !event.register(person)) {
            return null;
        }
        Ticket ticket = new BasicTicket(event.getTicketPrice(), event, person);
        for (String extra : extras) {
            if (extra.equalsIgnoreCase("VIP")) {
                ticket = new VipDecorator(ticket);
            } else if (extra.equalsIgnoreCase("MEAL")) {
                ticket = new MealDecorator(ticket);
            }
        }
        event.addTicket(ticket);
        return ticket;
    }

    public boolean changeDate(Event event, LocalDate newDate) {
        if (!event.isOpen() || newDate.isBefore(LocalDate.now())) {
            return false;
        }
        event.updateDate(newDate);
        notifyObservers(new ChangeEvent(ChangeType.EVENT_RESCHEDULED, event,
                "'" + event.getName() + "' was moved to " + newDate + "."));
        return true;
    }

    public void changeVenue(Event event, String newVenue) {
        event.updateVenue(newVenue);
        notifyObservers(new ChangeEvent(ChangeType.EVENT_VENUE_CHANGED, event,
                "'" + event.getName() + "' will now take place at " + newVenue + "."));
    }

    public void cancelEvent(Event event) {
        event.setStatus(EventStatus.CANCELLED);
        notifyObservers(new ChangeEvent(ChangeType.EVENT_CANCELLED, event,
                "'" + event.getName() + "' was cancelled."));
    }

    public void sendReminders(Event event) {
        notifyObservers(new ChangeEvent(ChangeType.EVENT_REMINDER, event,
                "Reminder: '" + event.getName() + "' is on " + event.getDate() + " at " + event.getVenue() + "."));
    }

    /** Marks the event done and credits every assigned volunteer with the hours worked. */
    public void completeEvent(Event event, double hoursPerVolunteer) {
        event.setStatus(EventStatus.COMPLETED);
        for (Assignment assignment : event.getAssignments()) {
            assignment.logHours(hoursPerVolunteer);
        }
        notifyObservers(new ChangeEvent(ChangeType.EVENT_COMPLETED, event,
                "'" + event.getName() + "' is completed. Thank you for taking part!"));
    }
}
