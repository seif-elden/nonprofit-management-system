package com.nonprofit.model.document;

import com.nonprofit.model.people.Volunteer;
import java.time.LocalDate;

/**
 * DECORATOR PATTERN - Concrete Component: the plain volunteer certificate.
 */
public class VolunteerCertificate extends Document {

    private Volunteer volunteer;
    private double totalHours;
    private LocalDate issuedDate;

    public VolunteerCertificate(Volunteer volunteer) {
        super("Volunteer Certificate - " + volunteer.getName());
        this.volunteer = volunteer;
        this.totalHours = volunteer.getTotalHours();
        this.issuedDate = LocalDate.now();
    }

    @Override
    public String render() {
        return "This certifies that " + volunteer.getName() + "\n"
                + "has contributed " + totalHours + " volunteer hours.\n"
                + "Issued on " + issuedDate + ".";
    }
}
