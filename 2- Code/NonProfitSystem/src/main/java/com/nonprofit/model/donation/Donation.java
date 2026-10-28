package com.nonprofit.model.donation;

import com.nonprofit.model.common.Identifiable;
import com.nonprofit.model.document.Document;
import com.nonprofit.model.people.Donor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * FACTORY METHOD PATTERN - Product.
 * The creator (DonationHandler) works with this type only and never
 * needs to know which concrete donation it received.
 */
public abstract class Donation implements Identifiable {

    private int id;
    private Donor donor;
    private Campaign campaign;
    private LocalDate date;
    private DonationStatus status;
    private String confirmation;
    private List<String> receiptOptions;
    private Document receipt;

    public Donation(int id, Donor donor, Campaign campaign) {
        this.id = id;
        this.donor = donor;
        this.campaign = campaign;
        this.date = LocalDate.now();
        this.status = DonationStatus.PENDING;
        this.receiptOptions = new ArrayList<>();
    }

    /** Kind of donation, e.g. "Monetary". */
    public abstract String getType();

    /** Checks the fields that this kind of donation requires. */
    public abstract boolean validate();

    /** Performs the donation and returns a confirmation text. */
    public abstract String process();

    /** Value of the donation in EGP. */
    public abstract double getValue();

    /** Short description, e.g. "500.00 EGP via Online". */
    public abstract String getSummary();

    /** Optional receipt parts the donor asked for: "TAX", "THANK_YOU". */
    public void setReceiptOptions(List<String> receiptOptions) {
        this.receiptOptions = receiptOptions;
    }

    @Override
    public int getId() {
        return id;
    }

    public Donor getDonor() {
        return donor;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public LocalDate getDate() {
        return date;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public List<String> getReceiptOptions() {
        return receiptOptions;
    }

    public Document getReceipt() {
        return receipt;
    }

    public void setReceipt(Document receipt) {
        this.receipt = receipt;
    }
}
