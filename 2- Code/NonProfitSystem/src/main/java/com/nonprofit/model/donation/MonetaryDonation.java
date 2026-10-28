package com.nonprofit.model.donation;

import com.nonprofit.model.payment.PaymentStrategy;
import com.nonprofit.model.people.Donor;

/**
 * FACTORY METHOD PATTERN - Concrete Product (money).
 * STRATEGY PATTERN - Context: delegates the payment to a PaymentStrategy
 * that can be swapped at runtime with the setter.
 */
public class MonetaryDonation extends Donation {

    private double amount;
    private PaymentStrategy paymentStrategy;

    public MonetaryDonation(int id, Donor donor, Campaign campaign, double amount) {
        super(id, donor, campaign);
        this.amount = amount;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    @Override
    public String getType() {
        return "Monetary";
    }

    @Override
    public boolean validate() {
        return amount > 0 && paymentStrategy != null;
    }

    /** One line of delegation instead of an if/else on the payment method. */
    @Override
    public String process() {
        return paymentStrategy.pay(amount);
    }

    @Override
    public double getValue() {
        return amount;
    }

    @Override
    public String getSummary() {
        String method = paymentStrategy == null ? "no payment method" : "via " + paymentStrategy.getMethodName();
        return String.format("%.2f EGP %s", amount, method);
    }

    public PaymentStrategy getPaymentStrategy() {
        return paymentStrategy;
    }
}
