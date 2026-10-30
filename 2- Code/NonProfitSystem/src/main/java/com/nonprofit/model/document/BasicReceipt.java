package com.nonprofit.model.document;

import com.nonprofit.model.donation.Donation;

/**
 * DECORATOR PATTERN - Concrete Component: the plain donation receipt.
 */
public class BasicReceipt extends Document {

    private Donation donation;

    public BasicReceipt(Donation donation) {
        super("Donation Receipt #" + donation.getId());
        this.donation = donation;
    }

    @Override
    public String render() {
        return "Receipt no.   : " + donation.getId() + "\n"
                + "Date          : " + donation.getDate() + "\n"
                + "Donor         : " + donation.getDonor().getName() + "\n"
                + "Campaign      : " + donation.getCampaign().getTitle() + "\n"
                + "Donation      : " + donation.getSummary() + "\n"
                + "Value (EGP)   : " + String.format("%.2f", donation.getValue()) + "\n"
                + "Confirmation  : " + donation.getConfirmation();
    }
}
