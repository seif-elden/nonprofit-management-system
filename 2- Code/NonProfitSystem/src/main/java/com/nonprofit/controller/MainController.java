package com.nonprofit.controller;

import com.nonprofit.ApplicationServices;
import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.people.Notification;
import com.nonprofit.model.people.Person;
import com.nonprofit.model.volunteer.VolunteerService;
import com.nonprofit.view.BeneficiaryView;
import com.nonprofit.view.DonationView;
import com.nonprofit.view.EventView;
import com.nonprofit.view.LookupView;
import com.nonprofit.view.MainView;
import com.nonprofit.view.PersonView;
import com.nonprofit.view.VolunteerView;

/**
 * MVC - Main controller and composition root: creates the services, plugs
 * the notification strategies in, subscribes the observers, then routes the
 * main menu to the module controllers.
 */
public class MainController {

    private MainView view;
    private DatabaseManager db;
    private PersonController personController;
    private DonationController donationController;
    private EventController eventController;
    private VolunteerController volunteerController;
    private BeneficiaryController beneficiaryController;
    private LookupController lookupController;

    public MainController(MainView view) {
        this.view = view;
        ApplicationServices services = ApplicationServices.getInstance();
        this.db = services.db;
        this.personController = new PersonController(new PersonView(), services.people);
        this.donationController = new DonationController(new DonationView(), services.donations, services.stats);
        this.eventController = new EventController(new EventView(), services.events, services.people,
                services.attendeeNotifications);
        this.volunteerController = new VolunteerController(new VolunteerView(), services.volunteers);
        this.beneficiaryController = new BeneficiaryController(new BeneficiaryView(), services.distributions);
        this.lookupController = new LookupController(new LookupView());
    }

    public void run() {
        int choice = view.show();
        while (choice != 0) {
            switch (choice) {
                case 1 -> personController.run();
                case 2 -> donationController.run();
                case 3 -> eventController.run();
                case 4 -> volunteerController.run();
                case 5 -> beneficiaryController.run();
                case 6 -> lookupController.run();
                case 7 -> showNotifications();
                case 8 -> view.showActivityLog(db.getActivityLog().getEntries());
                default -> view.showError("Unknown option.");
            }
            choice = view.show();
        }
        view.showMessage("Goodbye.");
    }

    private void showNotifications() {
        view.showPeople(db.getAllPeople());
        int id = view.readInt("Person id: ");
        for (Person person : db.getAllPeople()) {
            if (person.getId() == id) {
                view.showNotifications(person);
                for (Notification notification : person.getNotifications()) {
                    notification.markAsRead();
                }
                return;
            }
        }
        view.showError("No person with that id.");
    }
}
