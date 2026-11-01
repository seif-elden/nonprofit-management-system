package com.nonprofit.model.event;

import com.nonprofit.model.common.Identifiable;
import com.nonprofit.model.lookup.Skill;
import com.nonprofit.model.people.Person;
import com.nonprofit.model.people.Volunteer;
import com.nonprofit.model.event.ticket.Ticket;
import com.nonprofit.model.volunteer.Assignment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * FACTORY METHOD PATTERN - Product for event creation.
 * Common data and rules of every event; each subclass adds its own details.
 */
public abstract class Event implements Identifiable {

    private int id;
    private String name;
    private LocalDate date;
    private String venue;
    private int capacity;
    private double ticketPrice;
    private Skill requiredSkill;
    private EventStatus status;
    private List<Person> attendees;
    private List<Person> attended;
    private List<Assignment> assignments;
    private List<Ticket> tickets;

    public Event(int id, String name, LocalDate date, String venue, int capacity,
                 double ticketPrice, Skill requiredSkill) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.capacity = capacity;
        this.ticketPrice = ticketPrice;
        this.requiredSkill = requiredSkill;
        this.status = EventStatus.PLANNED;
        this.attendees = new ArrayList<>();
        this.attended = new ArrayList<>();
        this.assignments = new ArrayList<>();
        this.tickets = new ArrayList<>();
    }

    /** Event type name, e.g. "Fundraiser". */
    public abstract String getType();

    /** Type-specific details shown in lists. */
    public abstract String getDetails();

    /** Planned or ongoing events still accept people and volunteers. */
    public boolean isOpen() {
        return status == EventStatus.PLANNED || status == EventStatus.ONGOING;
    }

    /** Registers a person while seats remain and the event is open. */
    public boolean register(Person person) {
        if (!isOpen() || attendees.size() >= capacity || attendees.contains(person)) {
            return false;
        }
        attendees.add(person);
        return true;
    }

    /** Attendance can only be marked for registered people; the event becomes ONGOING. */
    public boolean markAttendance(Person person) {
        if (!isOpen() || !attendees.contains(person) || attended.contains(person)) {
            return false;
        }
        attended.add(person);
        status = EventStatus.ONGOING;
        return true;
    }

    public void updateDate(LocalDate newDate) {
        this.date = newDate;
    }

    public void updateVenue(String newVenue) {
        this.venue = newVenue;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public List<Volunteer> getVolunteers() {
        List<Volunteer> volunteers = new ArrayList<>();
        for (Assignment assignment : assignments) {
            volunteers.add(assignment.getVolunteer());
        }
        return volunteers;
    }

    @Override
    public int getId() {
        return id;
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

    public EventStatus getStatus() {
        return status;
    }

    public List<Person> getAttendees() {
        return attendees;
    }

    public List<Person> getAttended() {
        return attended;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
