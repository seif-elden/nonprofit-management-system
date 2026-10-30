package com.nonprofit.model.donation.factory;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.donation.Donation;
import com.nonprofit.model.donation.DonationForm;
import com.nonprofit.model.donation.DonationStatus;
import com.nonprofit.model.people.Donor;

/**
 * FACTORY METHOD PATTERN - Creator.
 * receiveDonation() is the workflow every donation follows; createDonation()
 * is the factory method that lets subclasses decide which Donation to build.
 */
public abstract class DonationHandler {

    /** Common workflow: create, validate, process, save. */
    public Donation receiveDonation(Donor donor, DonationForm form) {
        Donation donation = createDonation(donor, form);
        donation.setReceiptOptions(form.getReceiptOptions());

        if (form.getCampaign() == null || !donation.validate()) {
            donation.setStatus(DonationStatus.REJECTED);
            return donation;
        }
        donation.setConfirmation(donation.process());
        donation.setStatus(DonationStatus.COMPLETED);
        DatabaseManager.getInstance().getDonations().insert(donation);
        return donation;
    }

    /** The factory method. */
    protected abstract Donation createDonation(Donor donor, DonationForm form);
}
