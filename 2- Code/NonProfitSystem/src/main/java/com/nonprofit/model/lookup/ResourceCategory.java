package com.nonprofit.model.lookup;

/**
 * Lookup row: the kind of support a resource gives and a beneficiary
 * needs (Food Aid, Education, Medical ...).
 */
public class ResourceCategory extends LookupItem {

    private boolean essential;

    public ResourceCategory(int id, String name, String description, boolean essential) {
        super(id, name, description);
        this.essential = essential;
    }

    public boolean isEssential() {
        return essential;
    }

    @Override
    public String getTableName() {
        return "Resource Category";
    }
}
