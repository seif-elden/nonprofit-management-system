package com.nonprofit.model.people;

import com.nonprofit.model.lookup.ResourceCategory;
import java.util.ArrayList;
import java.util.List;

/**
 * A person (and their family) the organization supports.
 */
public class Beneficiary extends Person {

    private String address;
    private int familySize;
    private List<Need> needs;

    public Beneficiary(int id, String name, String email, String phone, String passwordHash,
                       String address, int familySize) {
        super(id, name, email, phone, passwordHash);
        this.address = address;
        this.familySize = familySize;
        this.needs = new ArrayList<>();
    }

    /** The beneficiary creates and owns its needs (composition). */
    public Need addNeed(int needId, ResourceCategory category, String description, int priority) {
        Need need = new Need(needId, category, description, priority);
        needs.add(need);
        return need;
    }

    public boolean hasNeed(ResourceCategory category) {
        for (Need need : needs) {
            if (need.getCategory() == category) {
                return true;
            }
        }
        return false;
    }

    public void setHousehold(String address, int familySize) {
        this.address = address;
        this.familySize = familySize;
    }

    public String getAddress() {
        return address;
    }

    public int getFamilySize() {
        return familySize;
    }

    public List<Need> getNeeds() {
        return needs;
    }

    @Override
    public String getRole() {
        return "Beneficiary";
    }
}
