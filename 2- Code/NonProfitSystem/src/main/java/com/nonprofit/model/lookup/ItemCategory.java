package com.nonprofit.model.lookup;

/**
 * Lookup row: category of an in-kind donation (Food, Clothes, Medicine ...).
 */
public class ItemCategory extends LookupItem {

    private boolean perishable;

    public ItemCategory(int id, String name, String description, boolean perishable) {
        super(id, name, description);
        this.perishable = perishable;
    }

    public boolean isPerishable() {
        return perishable;
    }

    @Override
    public String getTableName() {
        return "Item Category";
    }
}
