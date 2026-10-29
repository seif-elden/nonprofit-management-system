package com.nonprofit.view;

/**
 * MVC - View for maintaining the lookup tables (CRUD).
 */
public class LookupView extends View {

    /** First menu: which lookup table to manage. */
    @Override
    public int show() {
        showTitle("Lookup Tables");
        showMessage("1. Volunteer skills");
        showMessage("2. Donation item categories");
        showMessage("3. Resource categories");
        showMessage("0. Back");
        return readInt("Choose: ");
    }

    /** Second menu: the CRUD operation on the chosen table. */
    public int showTableMenu(String tableName) {
        showTitle(tableName);
        showMessage("1. List rows");
        showMessage("2. Add a row");
        showMessage("3. Edit a row");
        showMessage("4. Delete a row");
        showMessage("0. Back");
        return readInt("Choose: ");
    }
}
