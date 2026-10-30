package com.nonprofit.controller;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.lookup.Skill;
import com.nonprofit.model.people.Beneficiary;
import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.people.Donor;
import com.nonprofit.model.people.Person;
import com.nonprofit.model.people.PersonService;
import com.nonprofit.model.people.Volunteer;
import com.nonprofit.view.PersonView;

/**
 * MVC - Controller for sign-up, log-in and people management.
 */
public class PersonController {

    private PersonView view;
    private PersonService personService;

    public PersonController(PersonView view, PersonService personService) {
        this.view = view;
        this.personService = personService;
    }

    public void run() {
        int choice = view.show();
        while (choice != 0) {
            switch (choice) {
                case 1 -> signup();
                case 2 -> login();
                case 3 -> search();
                case 4 -> updateContact();
                case 5 -> deletePerson();
                default -> view.showError("Unknown option.");
            }
            choice = view.show();
        }
    }

    private void signup() {
        view.showSignupForm();
        int role = view.askRole();
        String type = role == 1 ? "DONOR" : role == 2 ? "VOLUNTEER" : role == 3 ? "BENEFICIARY" : "";
        String name = view.readText("Full name: ");
        String email = view.readText("E-mail: ");
        String phone = view.readText("Phone: ");
        String password = view.readText("Password (6+ characters): ");
        Person person = personService.register(type, name, email, phone, password);
        if (person == null) {
            view.showError("Sign-up failed: check the role, name, e-mail (must be new) and password length.");
            return;
        }
        Channel channel = view.askChannel();
        String social = channel == Channel.SOCIAL ? view.readText("Social handle: ") : "";
        person.updateContact(email, phone, social, channel);

        DatabaseManager db = DatabaseManager.getInstance();
        if (person instanceof Donor donor) {
            donor.setDonorDetails(view.readYesNo("Corporate donor?") ? "Corporate" : "Individual", view.readText("Tax id: "));
        } else if (person instanceof Volunteer volunteer) {
            view.showLookupItems(db.getSkills().findAll());
            for (String id : view.readText("Skill ids (comma separated): ").split(",")) {
                try {
                    Skill skill = db.getSkills().findById(Integer.parseInt(id.trim()));
                    if (skill != null) {
                        volunteer.addSkill(skill);
                    }
                } catch (NumberFormatException ignored) {
                    // skip anything that is not an id
                }
            }
            volunteer.addAvailability(view.readText("Availability (e.g. Weekends): "));
            volunteer.addPreference(view.readText("Preferred event type (Fundraiser/Workshop/Outreach): "));
        } else if (person instanceof Beneficiary beneficiary) {
            beneficiary.setHousehold(view.readText("Address: "), Math.max(1, view.readInt("Family size: ")));
        }
        personService.update(person);
        view.showMessage("Welcome, " + person.getName() + "! Your id is " + person.getId() + ".");
    }

    private void login() {
        view.showLoginForm();
        Person person = personService.authenticate(view.readText("E-mail: "), view.readText("Password: "));
        if (person == null) {
            view.showError("Wrong e-mail or password.");
        } else {
            view.showProfile(person);
        }
    }

    private void search() {
        view.showPeople(personService.searchByName(view.readText("Name contains: ")));
    }

    private void updateContact() {
        Person person = personService.findById(view.readInt("Person id: "));
        if (person == null) {
            view.showError("No person with that id.");
            return;
        }
        String email = view.readText("New e-mail: ");
        if (!email.contains("@") || !personService.isEmailAvailable(email, person)) {
            view.showError("That e-mail is invalid or already used.");
            return;
        }
        String phone = view.readText("New phone: ");
        Channel channel = view.askChannel();
        String social = view.readText("Social handle (blank for none): ");
        person.updateContact(email, phone, social, channel);
        personService.update(person);
        view.showProfile(person);
    }

    private void deletePerson() {
        int id = view.readInt("Person id to delete: ");
        if (personService.delete(id)) {
            view.showMessage("Person deleted.");
        } else {
            view.showError("Not deleted: unknown id, or the person has donations, assignments, distributions or registrations.");
        }
    }
}
