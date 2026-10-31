package com.nonprofit.view;

import com.nonprofit.model.event.Event;
import com.nonprofit.model.event.ticket.Ticket;
import java.util.List;

/**
 * MVC - View for events, tickets and attendance.
 */
public class EventView extends View {

    @Override
    public int show() {
        showTitle("Events");
        showMessage("1. List events");
        showMessage("2. Create an event");
        showMessage("3. Register a person");
        showMessage("4. Sell a ticket");
        showMessage("5. Mark attendance");
        showMessage("6. Change date");
        showMessage("7. Change venue");
        showMessage("8. Cancel an event");
        showMessage("9. Send reminders");
        showMessage("10. Complete an event");
        showMessage("0. Back");
        return readInt("Choose: ");
    }

    public int askEventType() {
        showMessage("Event type: 1. Fundraiser   2. Workshop   3. Outreach");
        return readInt("Choose: ");
    }

    public void showEventList(List<Event> events) {
        if (events.isEmpty()) {
            showMessage("(no events)");
        }
        for (Event e : events) {
            String skill = e.getRequiredSkill() == null ? "-" : e.getRequiredSkill().getName();
            System.out.printf("  %-4d %-22s %-10s %-11s %-11s %-22s seats %d/%d  attended %d  volunteers %d  ticket %.0f  needs %s  (%s)%n",
                    e.getId(), e.getName(), e.getType(), e.getDate(), e.getStatus(), e.getVenue(),
                    e.getAttendees().size(), e.getCapacity(), e.getAttended().size(), e.getVolunteers().size(),
                    e.getTicketPrice(), skill, e.getDetails());
        }
    }

    public void showTicket(Ticket ticket) {
        showMessage(String.format("  Ticket: %s - %.2f EGP", ticket.getDescription(), ticket.getPrice()));
    }
}
