package com.nonprofit.model.lookup;

import com.nonprofit.model.common.Identifiable;

/**
 * Base of every editable lookup (reference) table row. Admins create,
 * read, update and delete these rows at runtime instead of hard-coding them.
 */
public abstract class LookupItem implements Identifiable {

    private int id;
    private String name;
    private String description;

    public LookupItem(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /** Name of the lookup table this row belongs to. */
    public abstract String getTableName();

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
