package com.nonprofit.model.volunteer;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.document.Document;
import com.nonprofit.model.document.SignatureDecorator;
import com.nonprofit.model.document.ThankYouNoteDecorator;
import com.nonprofit.model.document.VolunteerCertificate;
import com.nonprofit.model.event.Event;
import com.nonprofit.model.lookup.Skill;
import com.nonprofit.model.people.Volunteer;
import java.util.ArrayList;
import java.util.List;

/**
 * Volunteer business logic: matching by skill and preference,
 * assignments, hours and certificates.
 */
public class VolunteerService {

    /** Assigns only if the event is open, the volunteer has the required skill and is not already assigned. */
    public Assignment assign(Volunteer volunteer, Event event, String role) {
        if (!event.isOpen() || event.getVolunteers().contains(volunteer)
                || (event.getRequiredSkill() != null && !volunteer.hasSkill(event.getRequiredSkill()))) {
            return null;
        }
        DatabaseManager db = DatabaseManager.getInstance();
        Assignment assignment = new Assignment(db.nextId(), volunteer, event, role);
        db.getAssignments().insert(assignment);
        event.addAssignment(assignment);
        return assignment;
    }

    public void logHours(Assignment assignment, double hours) {
        assignment.logHours(hours);
    }

    public List<Volunteer> findBySkill(Skill skill) {
        List<Volunteer> result = new ArrayList<>();
        for (Volunteer volunteer : DatabaseManager.getInstance().getVolunteers().findAll()) {
            if (volunteer.hasSkill(skill)) {
                result.add(volunteer);
            }
        }
        return result;
    }

    /** Qualified volunteers for an event; those who prefer this event type come first. */
    public List<Volunteer> suggestVolunteers(Event event) {
        List<Volunteer> qualified = event.getRequiredSkill() == null
                ? new ArrayList<>(DatabaseManager.getInstance().getVolunteers().findAll())
                : findBySkill(event.getRequiredSkill());
        List<Volunteer> result = new ArrayList<>();
        for (Volunteer volunteer : qualified) {
            if (volunteer.prefers(event.getType())) {
                result.add(volunteer);
            }
        }
        for (Volunteer volunteer : qualified) {
            if (!result.contains(volunteer)) {
                result.add(volunteer);
            }
        }
        return result;
    }

    public List<Assignment> getAssignmentsByEvent(Event event) {
        return event.getAssignments();
    }

    public List<Assignment> getAssignmentsByVolunteer(Volunteer volunteer) {
        List<Assignment> result = new ArrayList<>();
        for (Assignment assignment : DatabaseManager.getInstance().getAssignments().findAll()) {
            if (assignment.getVolunteer() == volunteer) {
                result.add(assignment);
            }
        }
        return result;
    }

    /** Decorator: plain certificate + thank-you note + signature. */
    public Document generateCertificate(Volunteer volunteer) {
        Document certificate = new VolunteerCertificate(volunteer);
        certificate = new ThankYouNoteDecorator(certificate, "We are grateful for your time and effort.");
        return new SignatureDecorator(certificate, "Volunteer Coordinator", DatabaseManager.getInstance().getOrganizationName());
    }
}
