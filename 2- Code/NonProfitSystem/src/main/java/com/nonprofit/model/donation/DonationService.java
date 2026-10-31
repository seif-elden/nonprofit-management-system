package com.nonprofit.model.donation;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.document.BasicReceipt;
import com.nonprofit.model.document.Document;
import com.nonprofit.model.document.SignatureDecorator;
import com.nonprofit.model.document.TaxInfoDecorator;
import com.nonprofit.model.document.ThankYouNoteDecorator;
import com.nonprofit.model.donation.factory.DonationHandler;
import com.nonprofit.model.observer.ChangeEvent;
import com.nonprofit.model.observer.ChangeType;
import com.nonprofit.model.observer.Observer;
import com.nonprofit.model.observer.Subject;
import com.nonprofit.model.people.Donor;
import java.util.ArrayList;
import java.util.List;

/**
 * Donation business logic.
 * OBSERVER PATTERN - Concrete Subject: every recorded donation is published
 * to the receipt, thank-you, statistics and activity-log observers.
 */
public class DonationService implements Subject {

    private List<Observer> observers;

    public DonationService() {
        observers = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(ChangeEvent change) {
        for (Observer observer : observers) {
            observer.update(this, change);
        }
    }

    /** The chosen creator (Factory Method) builds and processes the donation; success is published. */
    public Donation recordDonation(DonationHandler handler, Donor donor, DonationForm form) {
        Donation donation = handler.receiveDonation(donor, form);
        if (donation.getStatus() == DonationStatus.COMPLETED) {
            notifyObservers(new ChangeEvent(ChangeType.DONATION_RECORDED, donation,
                    donor.getName() + " donated " + donation.getSummary() + " to '" + donation.getCampaign().getTitle() + "'"));
        }
        return donation;
    }

    /** Decorator: the plain receipt, then only the extras asked for ("TAX", "THANK_YOU"), then the signature. */
    public Document buildReceipt(Donation donation, List<String> options) {
        DatabaseManager db = DatabaseManager.getInstance();
        Document receipt = new BasicReceipt(donation);
        for (String option : options) {
            if (option.equals("TAX")) {
                receipt = new TaxInfoDecorator(receipt, db.getTaxRegistrationNo());
            } else if (option.equals("THANK_YOU")) {
                receipt = new ThankYouNoteDecorator(receipt, "Thank you, " + donation.getDonor().getName()
                        + ", for supporting '" + donation.getCampaign().getTitle() + "'.");
            }
        }
        return new SignatureDecorator(receipt, "Finance Manager", db.getOrganizationName());
    }

    public List<Donation> getDonationsByDonor(Donor donor) {
        List<Donation> result = new ArrayList<>();
        for (Donation donation : DatabaseManager.getInstance().getDonations().findAll()) {
            if (donation.getDonor() == donor) {
                result.add(donation);
            }
        }
        return result;
    }

    /** Yearly statement of completed donations, for the donor's tax records. */
    public String generateTaxStatement(Donor donor, int year) {
        DatabaseManager db = DatabaseManager.getInstance();
        StringBuilder text = new StringBuilder();
        text.append(db.getOrganizationName()).append(" - tax reg. no. ").append(db.getTaxRegistrationNo()).append("\n");
        text.append("Annual donation statement ").append(year).append(" for ").append(donor.getName())
                .append(" (").append(donor.getDonorType()).append(", tax id ").append(donor.getTaxId()).append(")\n");
        double total = 0;
        for (Donation donation : getDonationsByDonor(donor)) {
            if (donation.getStatus() == DonationStatus.COMPLETED && donation.getDate().getYear() == year) {
                text.append("  ").append(donation.getDate()).append("  #").append(donation.getId()).append("  ")
                        .append(donation.getSummary()).append(String.format("  %.2f EGP", donation.getValue())).append("\n");
                total += donation.getValue();
            }
        }
        text.append(String.format("Total for %d: %.2f EGP", year, total));
        return text.toString();
    }
}
