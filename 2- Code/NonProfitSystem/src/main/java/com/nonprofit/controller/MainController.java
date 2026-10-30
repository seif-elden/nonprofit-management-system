package com.nonprofit.controller;

import com.nonprofit.model.beneficiary.DistributionService;
import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.donation.DonationService;
import com.nonprofit.model.event.EventService;
import com.nonprofit.model.notification.channels.EmailStrategy;
import com.nonprofit.model.notification.NotificationService;
import com.nonprofit.model.notification.channels.SmsStrategy;
import com.nonprofit.model.notification.channels.SocialMediaStrategy;
import com.nonprofit.model.observer.AttendeeNotificationObserver;
import com.nonprofit.model.observer.ReceiptObserver;
import com.nonprofit.model.observer.StatsObserver;
import com.nonprofit.model.observer.StockObserver;
import com.nonprofit.model.observer.ThankYouObserver;
import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.people.Notification;
import com.nonprofit.model.people.Person;
import com.nonprofit.model.people.PersonService;
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
        this.db = DatabaseManager.getInstance();

        // Strategy: one delivery algorithm per channel
        NotificationService notificationService = new NotificationService();
        notificationService.registerStrategy(Channel.EMAIL, new EmailStrategy());
        notificationService.registerStrategy(Channel.SMS, new SmsStrategy());
        notificationService.registerStrategy(Channel.SOCIAL, new SocialMediaStrategy());

        // Observer: subjects
        DonationService donationService = new DonationService();
        EventService eventService = new EventService();
        DistributionService distributionService = new DistributionService();
        PersonService personService = new PersonService();
        VolunteerService volunteerService = new VolunteerService();

        // Observer: each observer subscribes itself in its constructor
        new ReceiptObserver(donationService);
        new ThankYouObserver(donationService, notificationService);
        StatsObserver statsObserver = new StatsObserver(donationService);
        AttendeeNotificationObserver attendeeObserver = new AttendeeNotificationObserver(eventService, notificationService);
        new StockObserver(distributionService);
        db.getActivityLog().watch(donationService);
        db.getActivityLog().watch(eventService);
        db.getActivityLog().watch(distributionService);

        this.personController = new PersonController(new PersonView(), personService);
        this.donationController = new DonationController(new DonationView(), donationService, statsObserver);
        this.eventController = new EventController(new EventView(), eventService, personService, attendeeObserver);
        this.volunteerController = new VolunteerController(new VolunteerView(), volunteerService);
        this.beneficiaryController = new BeneficiaryController(new BeneficiaryView(), distributionService);
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
