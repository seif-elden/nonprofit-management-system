package com.nonprofit.controller;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.donation.Campaign;
import com.nonprofit.model.donation.Donation;
import com.nonprofit.model.donation.DonationForm;
import com.nonprofit.model.donation.DonationService;
import com.nonprofit.model.donation.DonationStatus;
import com.nonprofit.model.donation.factory.DonationHandler;
import com.nonprofit.model.donation.factory.InKindDonationHandler;
import com.nonprofit.model.donation.factory.MonetaryDonationHandler;
import com.nonprofit.model.observer.StatsObserver;
import com.nonprofit.model.payment.CashPayment;
import com.nonprofit.model.payment.CheckPayment;
import com.nonprofit.model.payment.OnlinePayment;
import com.nonprofit.model.payment.PaymentStrategy;
import com.nonprofit.model.people.Donor;
import com.nonprofit.view.DonationView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MVC - Controller for donations.
 * Picks the creator (Factory Method) and payment algorithm (Strategy) the
 * donor chose; the receipt (Decorator), thank-you message and totals are
 * produced by the observers of DonationService (Observer).
 */
public class DonationController {

    private DonationView view;
    private DonationService donationService;
    private StatsObserver statsObserver;
    private DatabaseManager db;

    public DonationController(DonationView view, DonationService donationService, StatsObserver statsObserver) {
        this.view = view;
        this.donationService = donationService;
        this.statsObserver = statsObserver;
        this.db = DatabaseManager.getInstance();
    }

    public void run() {
        int choice = view.show();
        while (choice != 0) {
            switch (choice) {
                case 1 -> submitDonation();
                case 2 -> loadDonations();
                case 3 -> showCampaigns();
                case 4 -> taxStatement();
                case 5 -> generateReceipt();
                default -> view.showError("Unknown option.");
            }
            choice = view.show();
        }
    }

    private void loadDonations() {
        view.showDonationList(db.getDonations().findAll());
    }

    private void showCampaigns() {
        view.showCampaigns(db.getCampaigns().findAll());
        view.showStats(statsObserver.getTotalRaised(), statsObserver.getDonationCount());
    }

    private void submitDonation() {
        view.showTitle("New Donation");
        view.showPeople(db.getDonors().findAll());
        Donor donor = db.getDonors().findById(view.readInt("Donor id: "));
        view.showCampaigns(db.getCampaigns().findAll());
        Campaign campaign = db.getCampaigns().findById(view.readInt("Campaign id: "));
        if (donor == null || campaign == null) {
            view.showError("Unknown donor or campaign.");
            return;
        }

        // Factory Method: choose the concrete creator; it decides which Donation to build.
        DonationHandler handler;
        DonationForm form;
        int type = view.askDonationType();
        if (type == 1) {
            double amount = view.readDouble("Amount (EGP): ");
            PaymentStrategy payment = choosePaymentStrategy(donor);
            if (payment == null) {
                view.showError("Unknown payment method.");
                return;
            }
            handler = new MonetaryDonationHandler();
            form = DonationForm.monetary(campaign, amount, payment);
        } else if (type == 2) {
            String item = view.readText("Item description: ");
            int quantity = view.readInt("Quantity: ");
            double value = view.readDouble("Estimated value (EGP): ");
            view.showLookupItems(db.getItemCategories().findAll());
            form = DonationForm.inKind(campaign, item, quantity, value,
                    db.getItemCategories().findById(view.readInt("Item category id: ")));
            handler = new InKindDonationHandler();
        } else {
            view.showError("Unknown donation type.");
            return;
        }
        form.withReceiptOptions(askReceiptOptions());

        Donation donation = donationService.recordDonation(handler, donor, form);
        if (donation.getStatus() == DonationStatus.REJECTED) {
            view.showError("Donation rejected: check the amount, quantity and category.");
            return;
        }
        view.showMessage(donation.getConfirmation());
        view.showReceipt(donation.getReceipt());
        view.showMessage("A thank-you message was sent to " + donor.getName() + " by " + donor.getPreferredChannel() + ".");
    }

    /** Strategy: the donor picks how to pay; each choice is a different algorithm object. */
    private PaymentStrategy choosePaymentStrategy(Donor donor) {
        int method = view.askPaymentMethod();
        return switch (method) {
            case 1 -> new OnlinePayment(donor.getName(), view.readText("Card number: "), "PayMob");
            case 2 -> new CheckPayment(view.readText("Check number: "), view.readText("Bank name: "));
            case 3 -> new CashPayment(view.readText("Received by (staff name): "));
            default -> null;
        };
    }

    private List<String> askReceiptOptions() {
        List<String> options = new ArrayList<>();
        if (view.readYesNo("Add tax information to the receipt?")) {
            options.add("TAX");
        }
        if (view.readYesNo("Add a thank-you note?")) {
            options.add("THANK_YOU");
        }
        return options;
    }

    private void taxStatement() {
        view.showPeople(db.getDonors().findAll());
        Donor donor = db.getDonors().findById(view.readInt("Donor id: "));
        if (donor == null) {
            view.showError("No donor with that id.");
            return;
        }
        int year = view.readInt("Year (e.g. " + LocalDate.now().getYear() + "): ");
        view.showTitle("Tax Statement");
        view.showMessage(donationService.generateTaxStatement(donor, year));
    }

    /** Re-issues the receipt of an existing donation with a new choice of extras. */
    private void generateReceipt() {
        loadDonations();
        Donation donation = db.getDonations().findById(view.readInt("Donation id: "));
        if (donation == null) {
            view.showError("No donation with that id.");
            return;
        }
        view.showReceipt(donationService.buildReceipt(donation, askReceiptOptions()));
    }
}
