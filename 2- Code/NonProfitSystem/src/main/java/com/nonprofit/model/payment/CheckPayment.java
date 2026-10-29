package com.nonprofit.model.payment;

import java.time.LocalDate;

/**
 * STRATEGY PATTERN - Concrete Strategy: payment by bank check.
 * Checks clear three days after they are recorded.
 */
public class CheckPayment implements PaymentStrategy {

    private String checkNumber;
    private String bankName;
    private LocalDate clearingDate;

    public CheckPayment(String checkNumber, String bankName) {
        this.checkNumber = checkNumber;
        this.bankName = bankName;
    }

    @Override
    public String pay(double amount) {
        clearingDate = LocalDate.now().plusDays(3);
        return String.format("Recorded check #%s from %s for %.2f EGP (clears on %s).",
                checkNumber, bankName, amount, clearingDate);
    }

    @Override
    public String getMethodName() {
        return "Check";
    }

    public LocalDate getClearingDate() {
        return clearingDate;
    }
}
