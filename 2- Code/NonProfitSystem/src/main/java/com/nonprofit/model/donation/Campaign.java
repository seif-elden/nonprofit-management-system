package com.nonprofit.model.donation;

import com.nonprofit.model.common.Identifiable;
import java.util.ArrayList;
import java.util.List;

/**
 * A fundraising campaign with a money goal.
 */
public class Campaign implements Identifiable {

    private int id;
    private String title;
    private double goalAmount;
    private double raisedAmount;
    private List<Donation> donations;

    public Campaign(int id, String title, double goalAmount) {
        this.id = id;
        this.title = title;
        this.goalAmount = goalAmount;
        this.raisedAmount = 0;
        this.donations = new ArrayList<>();
    }

    public void addDonation(Donation donation) {
        donations.add(donation);
        raisedAmount += donation.getValue();
    }

    public double getProgress() {
        return goalAmount == 0 ? 0 : raisedAmount * 100 / goalAmount;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getGoalAmount() {
        return goalAmount;
    }

    public double getRaisedAmount() {
        return raisedAmount;
    }

    public List<Donation> getDonations() {
        return donations;
    }
}
