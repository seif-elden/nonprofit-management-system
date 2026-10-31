package com.nonprofit.view;

import com.nonprofit.model.document.Document;
import com.nonprofit.model.donation.Campaign;
import com.nonprofit.model.donation.Donation;
import java.util.List;

/**
 * MVC - View for donations, campaigns and statements.
 */
public class DonationView extends View {

    @Override
    public int show() {
        showTitle("Donations");
        showMessage("1. Make a donation");
        showMessage("2. List all donations");
        showMessage("3. Campaign progress and totals");
        showMessage("4. Donor tax statement");
        showMessage("5. Re-issue a receipt");
        showMessage("0. Back");
        return readInt("Choose: ");
    }

    public int askDonationType() {
        showMessage("Donation type: 1. Money   2. Goods (in-kind)");
        return readInt("Choose: ");
    }

    public int askPaymentMethod() {
        showMessage("Payment method: 1. Online card   2. Check   3. Cash");
        return readInt("Choose: ");
    }

    public void showCampaigns(List<Campaign> campaigns) {
        for (Campaign campaign : campaigns) {
            System.out.printf("  %-4d %-25s raised %.2f / %.2f EGP (%.0f%%)%n", campaign.getId(),
                    campaign.getTitle(), campaign.getRaisedAmount(), campaign.getGoalAmount(), campaign.getProgress());
        }
    }

    public void showReceipt(Document receipt) {
        showDocument(receipt);
    }

    public void showStats(double totalRaised, int donationCount) {
        System.out.printf("  Organization total: %.2f EGP from %d donations%n", totalRaised, donationCount);
    }

    public void showDonationList(List<Donation> donations) {
        if (donations.isEmpty()) {
            showMessage("(no donations yet)");
        }
        for (Donation donation : donations) {
            System.out.printf("  %-4d %-12s %-16s %-40s %s%n", donation.getId(), donation.getDate(),
                    donation.getDonor().getName(), donation.getSummary(), donation.getStatus());
        }
    }
}
