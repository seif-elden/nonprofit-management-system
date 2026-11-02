package com.nonprofit.model.beneficiary;

import com.nonprofit.model.common.Identifiable;
import com.nonprofit.model.people.Beneficiary;
import java.time.LocalDate;

/**
 * A quantity of a resource given to a beneficiary on a date.
 */
public class Distribution implements Identifiable {

    private int id;
    private LocalDate date;
    private Beneficiary beneficiary;
    private Resource resource;
    private int quantity;

    public Distribution(int id, Beneficiary beneficiary, Resource resource, int quantity) {
        this.id = id;
        this.date = LocalDate.now();
        this.beneficiary = beneficiary;
        this.resource = resource;
        this.quantity = quantity;
    }

    @Override
    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    public Resource getResource() {
        return resource;
    }

    public int getQuantity() {
        return quantity;
    }
}
