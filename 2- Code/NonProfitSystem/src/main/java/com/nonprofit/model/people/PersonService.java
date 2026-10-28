package com.nonprofit.model.people;

import com.nonprofit.model.beneficiary.Distribution;
import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.donation.Donation;
import com.nonprofit.model.event.Event;
import com.nonprofit.model.volunteer.Assignment;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Accounts and people management: sign-up, log-in, search, update, delete.
 * Passwords are never stored in plain text, only their SHA-256 hash.
 */
public class PersonService {

    /**
     * Signs up a new person. type is "DONOR", "VOLUNTEER" or "BENEFICIARY";
     * role-specific details are filled in afterwards. Returns null if the
     * data is invalid or the e-mail is already used.
     */
    public Person register(String type, String name, String email, String phone, String password) {
        if (!canRegister(name, email, password)) {
            return null;
        }
        DatabaseManager db = DatabaseManager.getInstance();
        String hash = hashPassword(password);
        switch (type) {
            case "DONOR" -> {
                Donor donor = new Donor(db.nextId(), name, email, phone, hash, "Individual", "-");
                db.getDonors().insert(donor);
                return donor;
            }
            case "VOLUNTEER" -> {
                Volunteer volunteer = new Volunteer(db.nextId(), name, email, phone, hash);
                db.getVolunteers().insert(volunteer);
                return volunteer;
            }
            case "BENEFICIARY" -> {
                Beneficiary beneficiary = new Beneficiary(db.nextId(), name, email, phone, hash, "-", 1);
                db.getBeneficiaries().insert(beneficiary);
                return beneficiary;
            }
            default -> {
                return null;
            }
        }
    }

    /** Returns the person whose e-mail and password match, or null. */
    public Person authenticate(String email, String password) {
        String hash = hashPassword(password);
        for (Person person : DatabaseManager.getInstance().getAllPeople()) {
            if (person.getEmail().equalsIgnoreCase(email) && person.checkPassword(hash)) {
                return person;
            }
        }
        return null;
    }

    public Person findById(int id) {
        for (Person person : DatabaseManager.getInstance().getAllPeople()) {
            if (person.getId() == id) {
                return person;
            }
        }
        return null;
    }

    public List<Person> searchByName(String text) {
        List<Person> result = new ArrayList<>();
        for (Person person : DatabaseManager.getInstance().getAllPeople()) {
            if (person.getName().toLowerCase().contains(text.toLowerCase())) {
                result.add(person);
            }
        }
        return result;
    }

    /** True if no other person uses this e-mail. */
    public boolean isEmailAvailable(String email, Person except) {
        for (Person other : DatabaseManager.getInstance().getAllPeople()) {
            if (other != except && other.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }
        return true;
    }

    /** Saves a changed person back to its table. */
    public boolean update(Person person) {
        DatabaseManager db = DatabaseManager.getInstance();
        if (person instanceof Donor donor) {
            return db.getDonors().update(donor);
        } else if (person instanceof Volunteer volunteer) {
            return db.getVolunteers().update(volunteer);
        } else if (person instanceof Beneficiary beneficiary) {
            return db.getBeneficiaries().update(beneficiary);
        }
        return false;
    }

    /** A person with donations, assignments, distributions or event registrations cannot be deleted. */
    public boolean delete(int id) {
        Person person = findById(id);
        if (person == null || isInUse(person)) {
            return false;
        }
        DatabaseManager db = DatabaseManager.getInstance();
        return db.getDonors().delete(id) || db.getVolunteers().delete(id) || db.getBeneficiaries().delete(id);
    }

    public boolean isInUse(Person person) {
        DatabaseManager db = DatabaseManager.getInstance();
        for (Donation donation : db.getDonations().findAll()) {
            if (donation.getDonor() == person) {
                return true;
            }
        }
        for (Assignment assignment : db.getAssignments().findAll()) {
            if (assignment.getVolunteer() == person) {
                return true;
            }
        }
        for (Distribution distribution : db.getDistributions().findAll()) {
            if (distribution.getBeneficiary() == person) {
                return true;
            }
        }
        for (Event event : db.getEvents().findAll()) {
            if (event.getAttendees().contains(person)) {
                return true;
            }
        }
        return false;
    }

    private boolean canRegister(String name, String email, String password) {
        return name != null && !name.isBlank() && email != null && email.contains("@")
                && password != null && password.length() >= 6 && isEmailAvailable(email, null);
    }

    public static String hashPassword(String password) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
