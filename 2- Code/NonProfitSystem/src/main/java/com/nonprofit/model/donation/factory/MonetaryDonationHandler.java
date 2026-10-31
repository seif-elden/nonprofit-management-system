package com.nonprofit.model.donation.factory;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.donation.Donation;
import com.nonprofit.model.donation.DonationForm;
import com.nonprofit.model.donation.MonetaryDonation;
import com.nonprofit.model.people.Donor;

/**
 * FACTORY METHOD PATTERN - Concrete Creator for money donations.
 */
public class MonetaryDonationHandler extends DonationHandler {

    @Override
    protected Donation createDonation(Donor donor, DonationForm form) {
        MonetaryDonation donation = new MonetaryDonation(DatabaseManager.getInstance().nextId(),
                donor, form.getCampaign(), form.getAmount());
        donation.setPaymentStrategy(form.getPaymentStrategy());   // Strategy chosen by the donor
        return donation;
    }
}
