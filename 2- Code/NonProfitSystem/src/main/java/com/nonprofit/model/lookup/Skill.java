package com.nonprofit.model.lookup;

/**
 * Lookup row: a volunteer skill (First Aid, Teaching, Driving ...).
 */
public class Skill extends LookupItem {

    private boolean requiresTraining;

    public Skill(int id, String name, String description, boolean requiresTraining) {
        super(id, name, description);
        this.requiresTraining = requiresTraining;
    }

    public boolean isRequiresTraining() {
        return requiresTraining;
    }

    @Override
    public String getTableName() {
        return "Skill";
    }
}
