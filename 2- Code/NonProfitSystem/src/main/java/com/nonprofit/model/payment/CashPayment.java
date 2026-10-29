package com.nonprofit.model.payment;

/**
 * STRATEGY PATTERN - Concrete Strategy: cash handed to a staff member.
 */
public class CashPayment implements PaymentStrategy {

    private String receivedBy;

    public CashPayment(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    @Override
    public String pay(double amount) {
        return String.format("%.2f EGP received in cash by %s.", amount, receivedBy);
    }

    @Override
    public String getMethodName() {
        return "Cash";
    }
}
