package com.nonprofit.model.payment;

/**
 * STRATEGY PATTERN - Concrete Strategy: card payment through an online
 * gateway (simulated in Phase 1). Only the last 4 card digits are shown.
 */
public class OnlinePayment implements PaymentStrategy {

    private static int transactionCounter = 1000;

    private String cardHolder;
    private String cardNumber;
    private String gateway;
    private String transactionId;

    public OnlinePayment(String cardHolder, String cardNumber, String gateway) {
        this.cardHolder = cardHolder;
        this.cardNumber = cardNumber;
        this.gateway = gateway;
    }

    @Override
    public String pay(double amount) {
        transactionCounter++;
        transactionId = "TX-" + transactionCounter;
        String lastDigits = cardNumber.length() > 4 ? cardNumber.substring(cardNumber.length() - 4) : cardNumber;
        return String.format("Charged %.2f EGP to %s's card ending in %s via %s (transaction %s).",
                amount, cardHolder, lastDigits, gateway, transactionId);
    }

    @Override
    public String getMethodName() {
        return "Online";
    }

    public String getTransactionId() {
        return transactionId;
    }
}
