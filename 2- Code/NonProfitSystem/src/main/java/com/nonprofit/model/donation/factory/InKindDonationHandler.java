package com.nonprofit.model.donation.factory;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.donation.Donation;
import com.nonprofit.model.donation.DonationForm;
import com.nonprofit.model.donation.InKindDonation;
import com.nonprofit.model.people.Donor;

/**
 * FACTORY METHOD PATTERN - Concrete Creator for goods donations.
 */
public class InKindDonationHandler extends DonationHandler {

    @Override
    protected Donation createDonation(Donor donor, DonationForm form) {
        return new InKindDonation(DatabaseManager.getInstance().nextId(), donor, form.getCampaign(),
                form.getItemDescription(), form.getQuantity(), form.getEstimatedValue(), form.getItemCategory());
    }
}
