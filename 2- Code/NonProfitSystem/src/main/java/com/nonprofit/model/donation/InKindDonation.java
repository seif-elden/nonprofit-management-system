package com.nonprofit.model.donation;

import com.nonprofit.model.lookup.ItemCategory;
import com.nonprofit.model.people.Donor;

/**
 * FACTORY METHOD PATTERN - Concrete Product (goods instead of money).
 */
public class InKindDonation extends Donation {

    private String itemDescription;
    private int quantity;
    private double estimatedValue;
    private ItemCategory category;

    public InKindDonation(int id, Donor donor, Campaign campaign, String itemDescription,
                          int quantity, double estimatedValue, ItemCategory category) {
        super(id, donor, campaign);
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.estimatedValue = estimatedValue;
        this.category = category;
    }

    @Override
    public String getType() {
        return "In-kind";
    }

    @Override
    public boolean validate() {
        return itemDescription != null && !itemDescription.isBlank() && quantity > 0
                && estimatedValue >= 0 && category != null;
    }

    @Override
    public String process() {
        String storage = category.isPerishable() ? "cold storage" : "the main warehouse";
        return quantity + " x " + itemDescription + " received and sent to " + storage + ".";
    }

    @Override
    public double getValue() {
        return estimatedValue;
    }

    @Override
    public String getSummary() {
        return quantity + " x " + itemDescription + " (" + category.getName() + ")";
    }

    public ItemCategory getCategory() {
        return category;
    }
}
