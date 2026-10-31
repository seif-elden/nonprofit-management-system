package com.nonprofit.model.donation;

import com.nonprofit.model.lookup.ItemCategory;
import com.nonprofit.model.payment.PaymentStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * The data a donor fills in. Passed to the factory method so each concrete
 * creator reads the fields it needs (money fields or goods fields).
 */
public class DonationForm {

    private Campaign campaign;
    private double amount;
    private PaymentStrategy paymentStrategy;
    private String itemDescription;
    private int quantity;
    private double estimatedValue;
    private ItemCategory itemCategory;
    private List<String> receiptOptions;

    private DonationForm(Campaign campaign) {
        this.campaign = campaign;
        this.receiptOptions = new ArrayList<>();
    }

    /** Form for a money donation. */
    public static DonationForm monetary(Campaign campaign, double amount, PaymentStrategy paymentStrategy) {
        DonationForm form = new DonationForm(campaign);
        form.amount = amount;
        form.paymentStrategy = paymentStrategy;
        return form;
    }

    /** Form for a goods donation. */
    public static DonationForm inKind(Campaign campaign, String itemDescription, int quantity,
                                      double estimatedValue, ItemCategory itemCategory) {
        DonationForm form = new DonationForm(campaign);
        form.itemDescription = itemDescription;
        form.quantity = quantity;
        form.estimatedValue = estimatedValue;
        form.itemCategory = itemCategory;
        return form;
    }

    /** Which optional parts the donor wants on the receipt: "TAX", "THANK_YOU". */
    public DonationForm withReceiptOptions(List<String> receiptOptions) {
        this.receiptOptions = receiptOptions;
        return this;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStrategy getPaymentStrategy() {
        return paymentStrategy;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public List<String> getReceiptOptions() {
        return receiptOptions;
    }
}
