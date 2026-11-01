package com.nonprofit.controller;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.event.Event;
import com.nonprofit.model.people.Volunteer;
import com.nonprofit.model.volunteer.Assignment;
import com.nonprofit.model.volunteer.VolunteerService;
import com.nonprofit.view.VolunteerView;

/**
 * MVC - Controller for volunteers: matching, assignments, hours, certificates.
 */
public class VolunteerController {

    private VolunteerView view;
    private VolunteerService volunteerService;
    private DatabaseManager db;

    public VolunteerController(VolunteerView view, VolunteerService volunteerService) {
        this.view = view;
        this.volunteerService = volunteerService;
        this.db = DatabaseManager.getInstance();
    }

    public void run() {
        int choice = view.show();
        while (choice != 0) {
            switch (choice) {
                case 1 -> loadVolunteers();
                case 2 -> suggest();
                case 3 -> assign();
                case 4 -> logHours();
                case 5 -> showAssignments();
                case 6 -> issueCertificate();
                default -> view.showError("Unknown option.");
            }
            choice = view.show();
        }
    }

    private void loadVolunteers() {
        view.showVolunteers(db.getVolunteers().findAll());
    }

    private void suggest() {
        Event event = chooseEvent();
        if (event != null) {
            view.showMessage("Qualified volunteers (preferred event type first):");
            view.showVolunteers(volunteerService.suggestVolunteers(event));
        }
    }

    private void assign() {
        Event event = chooseEvent();
        if (event == null) {
            return;
        }
        loadVolunteers();
        Volunteer volunteer = db.getVolunteers().findById(view.readInt("Volunteer id: "));
        if (volunteer == null) {
            view.showError("No volunteer with that id.");
            return;
        }
        Assignment assignment = volunteerService.assign(volunteer, event, view.readText("Role: "));
        if (assignment == null) {
            view.showError("Not assigned: the event is closed, the volunteer lacks the required skill, or is already assigned.");
        } else {
            view.showMessage(volunteer.getName() + " assigned to " + event.getName() + " (assignment " + assignment.getId() + ").");
        }
    }

    private void logHours() {
        view.showAssignments(db.getAssignments().findAll());
        Assignment assignment = db.getAssignments().findById(view.readInt("Assignment id: "));
        double hours = assignment == null ? 0 : view.readDouble("Hours: ");
        if (assignment == null || hours <= 0) {
            view.showError("Unknown assignment or invalid hours.");
            return;
        }
        volunteerService.logHours(assignment, hours);
        view.showMessage(assignment.getVolunteer().getName() + " now has " + assignment.getVolunteer().getTotalHours() + " hours.");
    }

    private void showAssignments() {
        Event event = chooseEvent();
        if (event != null) {
            view.showAssignments(volunteerService.getAssignmentsByEvent(event));
        }
    }

    private void issueCertificate() {
        loadVolunteers();
        Volunteer volunteer = db.getVolunteers().findById(view.readInt("Volunteer id: "));
        if (volunteer == null) {
            view.showError("No volunteer with that id.");
        } else if (volunteer.getTotalHours() <= 0) {
            view.showError(volunteer.getName() + " has no recorded hours yet.");
        } else {
            view.showCertificate(volunteerService.generateCertificate(volunteer));
        }
    }

    private Event chooseEvent() {
        for (Event event : db.getEvents().findAll()) {
            view.showMessage(String.format("  %-4d %-22s %-10s %s %s", event.getId(), event.getName(),
                    event.getType(), event.getDate(), event.getStatus()));
        }
        Event event = db.getEvents().findById(view.readInt("Event id: "));
        if (event == null) {
            view.showError("No event with that id.");
        }
        return event;
    }
}
