package com.nonprofit.model.observer;

import com.nonprofit.model.donation.Donation;

/**
 * OBSERVER PATTERN - Concrete Observer: keeps the fundraising totals
 * (organization total, donor total, campaign progress) up to date.
 */
public class StatsObserver implements Observer {

    private double totalRaised;
    private int donationCount;

    public StatsObserver(Subject donationService) {
        donationService.addObserver(this);
    }

    @Override
    public void update(Subject source, ChangeEvent change) {
        if (change.getType() == ChangeType.DONATION_RECORDED) {
            Donation donation = (Donation) change.getPayload();
            totalRaised += donation.getValue();
            donationCount++;
            donation.getDonor().addToTotal(donation.getValue());
            donation.getCampaign().addDonation(donation);
        }
    }

    public double getTotalRaised() {
        return totalRaised;
    }

    public int getDonationCount() {
        return donationCount;
    }
}
