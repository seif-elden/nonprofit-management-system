package com.nonprofit.model.observer;

import com.nonprofit.model.donation.Donation;
import com.nonprofit.model.donation.DonationService;

/**
 * OBSERVER PATTERN - Concrete Observer: issues the receipt for every
 * recorded donation. The receipt is built by DonationService (Decorator
 * pattern) and attached to the donation; the view shows it, nothing is
 * printed here.
 */
public class ReceiptObserver implements Observer {

    private DonationService donationService;

    public ReceiptObserver(DonationService donationService) {
        this.donationService = donationService;
        donationService.addObserver(this);
    }

    @Override
    public void update(Subject source, ChangeEvent change) {
        if (change.getType() == ChangeType.DONATION_RECORDED) {
            Donation donation = (Donation) change.getPayload();
            donation.setReceipt(donationService.buildReceipt(donation, donation.getReceiptOptions()));
        }
    }
}
