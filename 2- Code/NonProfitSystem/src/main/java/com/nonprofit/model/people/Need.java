package com.nonprofit.model.people;

import com.nonprofit.model.lookup.ResourceCategory;

/**
 * One need of a beneficiary. Created only by Beneficiary.addNeed(),
 * so it cannot exist without its beneficiary (composition).
 */
public class Need {

    private int id;
    private ResourceCategory category;
    private String description;
    private int priority;

    public Need(int id, ResourceCategory category, String description, int priority) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public ResourceCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    /** 1 = most urgent. */
    public int getPriority() {
        return priority;
    }
}
