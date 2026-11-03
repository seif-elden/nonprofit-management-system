package com.nonprofit;

import com.nonprofit.model.beneficiary.DistributionService;
import com.nonprofit.model.donation.DonationService;
import com.nonprofit.model.event.EventService;
import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.notification.NotificationService;
import com.nonprofit.model.notification.channels.EmailStrategy;
import com.nonprofit.model.notification.channels.SmsStrategy;
import com.nonprofit.model.notification.channels.SocialMediaStrategy;
import com.nonprofit.model.observer.AttendeeNotificationObserver;
import com.nonprofit.model.observer.ReceiptObserver;
import com.nonprofit.model.observer.StatsObserver;
import com.nonprofit.model.observer.StockObserver;
import com.nonprofit.model.observer.ThankYouObserver;
import com.nonprofit.model.people.PersonService;
import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.volunteer.VolunteerService;

/** Shared composition root for the console and browser interfaces. */
public final class ApplicationServices {
    private static final ApplicationServices INSTANCE = new ApplicationServices();

    public final DatabaseManager db = DatabaseManager.getInstance();
    public final PersonService people = new PersonService();
    public final DonationService donations = new DonationService();
    public final EventService events = new EventService();
    public final VolunteerService volunteers = new VolunteerService();
    public final DistributionService distributions = new DistributionService();
    public final StatsObserver stats;
    public final AttendeeNotificationObserver attendeeNotifications;

    private ApplicationServices() {
        NotificationService notifications = new NotificationService();
        notifications.registerStrategy(Channel.EMAIL, new EmailStrategy());
        notifications.registerStrategy(Channel.SMS, new SmsStrategy());
        notifications.registerStrategy(Channel.SOCIAL, new SocialMediaStrategy());
        new ReceiptObserver(donations);
        new ThankYouObserver(donations, notifications);
        stats = new StatsObserver(donations);
        attendeeNotifications = new AttendeeNotificationObserver(events, notifications);
        new StockObserver(distributions);
        db.getActivityLog().watch(donations);
        db.getActivityLog().watch(events);
        db.getActivityLog().watch(distributions);
    }

    public static ApplicationServices getInstance() {
        return INSTANCE;
    }
}
