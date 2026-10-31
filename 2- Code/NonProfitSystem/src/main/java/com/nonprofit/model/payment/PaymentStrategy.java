package com.nonprofit.model.payment;

/**
 * STRATEGY PATTERN - Strategy interface.
 * A family of interchangeable payment algorithms for money donations.
 */
public interface PaymentStrategy {

    /** Pays the amount and returns a confirmation text. */
    String pay(double amount);

    String getMethodName();
}
