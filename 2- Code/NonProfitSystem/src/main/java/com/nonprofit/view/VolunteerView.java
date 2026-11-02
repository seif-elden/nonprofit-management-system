package com.nonprofit.view;

import com.nonprofit.model.document.Document;
import com.nonprofit.model.lookup.Skill;
import com.nonprofit.model.people.Volunteer;
import com.nonprofit.model.volunteer.Assignment;
import java.util.List;

/**
 * MVC - View for volunteers, assignments and certificates.
 */
public class VolunteerView extends View {

    @Override
    public int show() {
        showTitle("Volunteers");
        showMessage("1. List volunteers");
        showMessage("2. Suggest volunteers for an event");
        showMessage("3. Assign a volunteer to an event");
        showMessage("4. Log extra hours on an assignment");
        showMessage("5. Assignments of an event");
        showMessage("6. Issue a certificate");
        showMessage("0. Back");
        return readInt("Choose: ");
    }

    public void showVolunteers(List<Volunteer> volunteers) {
        if (volunteers.isEmpty()) {
            showMessage("(no volunteers)");
        }
        for (Volunteer v : volunteers) {
            StringBuilder skills = new StringBuilder();
            for (Skill skill : v.getSkills()) {
                skills.append(skill.getName()).append(", ");
            }
            System.out.printf("  %-4d %-18s skills [%s] available %s prefers %s  hours %.1f%n", v.getId(), v.getName(),
                    skills.length() > 2 ? skills.substring(0, skills.length() - 2) : "", v.getAvailability(),
                    v.getPreferences(), v.getTotalHours());
        }
    }

    public void showCertificate(Document certificate) {
        showDocument(certificate);
    }

    public void showAssignments(List<Assignment> assignments) {
        if (assignments.isEmpty()) {
            showMessage("(no assignments)");
        }
        for (Assignment a : assignments) {
            System.out.printf("  %-4d %-18s %-22s role %-14s %s  hours %.1f%n", a.getId(), a.getVolunteer().getName(),
                    a.getEvent().getName(), a.getRole(), a.getDate(), a.getHoursWorked());
        }
    }
}
