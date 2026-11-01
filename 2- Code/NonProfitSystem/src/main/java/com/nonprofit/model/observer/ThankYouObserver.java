package com.nonprofit.model.observer;

import com.nonprofit.model.donation.Donation;
import com.nonprofit.model.notification.Message;
import com.nonprofit.model.notification.NotificationService;

/**
 * OBSERVER PATTERN - Concrete Observer: thanks the donor through their
 * preferred channel (uses the notification Strategy).
 */
public class ThankYouObserver implements Observer {

    private NotificationService notificationService;

    public ThankYouObserver(Subject donationService, NotificationService notificationService) {
        this.notificationService = notificationService;
        donationService.addObserver(this);
    }

    @Override
    public void update(Subject source, ChangeEvent change) {
        if (change.getType() == ChangeType.DONATION_RECORDED) {
            Donation donation = (Donation) change.getPayload();
            notificationService.sendNotification(donation.getDonor(), new Message("Thank you for your donation",
                    "Dear " + donation.getDonor().getName() + ", we received " + donation.getSummary()
                            + " for '" + donation.getCampaign().getTitle() + "'. Thank you!"));
        }
    }
}
