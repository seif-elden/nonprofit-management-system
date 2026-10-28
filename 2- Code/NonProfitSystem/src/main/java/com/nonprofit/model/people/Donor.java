package com.nonprofit.model.people;

/**
 * A person or company that gives money or goods.
 */
public class Donor extends Person {

    private String donorType;
    private String taxId;
    private double totalDonated;

    public Donor(int id, String name, String email, String phone, String passwordHash,
                 String donorType, String taxId) {
        super(id, name, email, phone, passwordHash);
        this.donorType = donorType;
        this.taxId = taxId;
        this.totalDonated = 0;
    }

    public void addToTotal(double value) {
        totalDonated += value;
    }

    public void setDonorDetails(String donorType, String taxId) {
        this.donorType = donorType;
        this.taxId = taxId;
    }

    public String getDonorType() {
        return donorType;
    }

    public String getTaxId() {
        return taxId;
    }

    public double getTotalDonated() {
        return totalDonated;
    }

    @Override
    public String getRole() {
        return "Donor";
    }
}
