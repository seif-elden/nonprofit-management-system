package com.nonprofit.controller;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.event.Event;
import com.nonprofit.model.event.EventForm;
import com.nonprofit.model.event.EventService;
import com.nonprofit.model.event.factory.EventCreator;
import com.nonprofit.model.event.factory.FundraiserCreator;
import com.nonprofit.model.event.factory.OutreachCreator;
import com.nonprofit.model.event.factory.WorkshopCreator;
import com.nonprofit.model.lookup.Skill;
import com.nonprofit.model.observer.AttendeeNotificationObserver;
import com.nonprofit.model.people.Person;
import com.nonprofit.model.people.PersonService;
import com.nonprofit.model.event.ticket.Ticket;
import com.nonprofit.view.EventView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MVC - Controller for events.
 * Creation uses the Factory Method; tickets use the Decorator; every
 * change is published by EventService to its observers.
 */
public class EventController {

    private EventView view;
    private EventService eventService;
    private PersonService personService;
    private AttendeeNotificationObserver attendeeObserver;
    private DatabaseManager db;

    public EventController(EventView view, EventService eventService, PersonService personService,
                           AttendeeNotificationObserver attendeeObserver) {
        this.view = view;
        this.eventService = eventService;
        this.personService = personService;
        this.attendeeObserver = attendeeObserver;
        this.db = DatabaseManager.getInstance();
    }

    public void run() {
        int choice = view.show();
        while (choice != 0) {
            switch (choice) {
                case 1 -> loadEvents();
                case 2 -> createEvent();
                case 3 -> registerAttendee();
                case 4 -> sellTicket();
                case 5 -> markAttendance();
                case 6 -> changeDate();
                case 7 -> changeVenue();
                case 8 -> cancelEvent();
                case 9 -> sendReminders();
                case 10 -> completeEvent();
                default -> view.showError("Unknown option.");
            }
            choice = view.show();
        }
    }

    private void loadEvents() {
        view.showEventList(db.getEvents().findAll());
    }

    private void createEvent() {
        view.showTitle("New Event");
        int type = view.askEventType();
        // Factory Method: choose the concrete creator; it decides which Event subclass to build.
        EventCreator creator = switch (type) {
            case 1 -> new FundraiserCreator();
            case 2 -> new WorkshopCreator();
            case 3 -> new OutreachCreator();
            default -> null;
        };
        if (creator == null) {
            view.showError("Unknown event type.");
            return;
        }
        String name = view.readText("Name: ");
        LocalDate date = view.readDate("Date");
        String venue = view.readText("Venue: ");
        int capacity = view.readInt("Capacity: ");
        double price = view.readDouble("Ticket price (0 = free): ");
        view.showLookupItems(db.getSkills().findAll());
        Skill skill = db.getSkills().findById(view.readInt("Required volunteer skill id (0 = none): "));
        EventForm form = new EventForm(name, date, venue, capacity, price, skill);
        if (type == 1) {
            form.setFundraiserDetails(view.readDouble("Target amount (EGP): "));
        } else if (type == 2) {
            form.setWorkshopDetails(view.readText("Trainer: "), view.readText("Topic: "));
        } else {
            form.setOutreachDetails(view.readText("Location / area served: "), view.readInt("Team size: "));
        }
        Event event = eventService.createEvent(creator, form);
        if (event == null) {
            view.showError("Event not created: name required, date not in the past, capacity above 0.");
        } else {
            view.showMessage(event.getType() + " created with id " + event.getId() + ".");
        }
    }

    private void registerAttendee() {
        Event event = chooseEvent();
        Person person = event == null ? null : choosePerson();
        if (person != null) {
            if (eventService.registerAttendee(event, person)) {
                view.showMessage(person.getName() + " is registered and will get updates about this event.");
            } else {
                view.showError("Not registered: already registered, event full, or event closed.");
            }
        }
    }

    private void sellTicket() {
        Event event = chooseEvent();
        Person person = event == null ? null : choosePerson();
        if (person == null) {
            return;
        }
        List<String> extras = new ArrayList<>();
        if (view.readYesNo("VIP seating (+250 EGP)?")) {
            extras.add("VIP");
        }
        if (view.readYesNo("Dinner (+150 EGP)?")) {
            extras.add("MEAL");
        }
        Ticket ticket = eventService.sellTicket(event, person, extras);
        if (ticket == null) {
            view.showError("No ticket: event full or closed.");
        } else {
            view.showTicket(ticket);
        }
    }

    private void markAttendance() {
        Event event = chooseEvent();
        if (event == null) {
            return;
        }
        view.showPeople(event.getAttendees());
        Person person = personService.findById(view.readInt("Attendee id: "));
        if (person != null && eventService.markAttendance(event, person)) {
            view.showMessage(person.getName() + " marked as attended.");
        } else {
            view.showError("Not marked: the person is not registered, already marked, or the event is closed.");
        }
    }

    private void changeDate() {
        Event event = chooseEvent();
        if (event != null) {
            if (eventService.changeDate(event, view.readDate("New date"))) {
                reportNotified();
            } else {
                view.showError("Date not changed: the event is closed or the date is in the past.");
            }
        }
    }

    private void changeVenue() {
        Event event = chooseEvent();
        if (event != null) {
            eventService.changeVenue(event, view.readText("New venue: "));
            reportNotified();
        }
    }

    private void cancelEvent() {
        Event event = chooseEvent();
        if (event != null && event.isOpen()) {
            eventService.cancelEvent(event);
            reportNotified();
        } else if (event != null) {
            view.showError("This event is already closed.");
        }
    }

    private void sendReminders() {
        Event event = chooseEvent();
        if (event != null) {
            eventService.sendReminders(event);
            reportNotified();
        }
    }

    private void completeEvent() {
        Event event = chooseEvent();
        if (event == null) {
            return;
        }
        if (!event.isOpen()) {
            view.showError("This event is already closed.");
            return;
        }
        eventService.completeEvent(event, view.readDouble("Hours worked by each volunteer: "));
        reportNotified();
        view.showMessage("Volunteer hours were credited; certificates can be issued from the Volunteers menu.");
    }

    private void reportNotified() {
        view.showMessage("Done. " + attendeeObserver.getLastReached() + " attendee(s)/volunteer(s) were notified.");
    }

    private Event chooseEvent() {
        loadEvents();
        Event event = db.getEvents().findById(view.readInt("Event id: "));
        if (event == null) {
            view.showError("No event with that id.");
        }
        return event;
    }

    private Person choosePerson() {
        view.showPeople(db.getAllPeople());
        Person person = personService.findById(view.readInt("Person id: "));
        if (person == null) {
            view.showError("No person with that id.");
        }
        return person;
    }
}
