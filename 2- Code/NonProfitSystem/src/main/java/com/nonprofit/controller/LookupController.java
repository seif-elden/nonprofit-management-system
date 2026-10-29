package com.nonprofit.controller;

import com.nonprofit.model.beneficiary.Resource;
import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.persistence.GenericDAO;
import com.nonprofit.model.donation.Donation;
import com.nonprofit.model.donation.InKindDonation;
import com.nonprofit.model.event.Event;
import com.nonprofit.model.lookup.ItemCategory;
import com.nonprofit.model.lookup.LookupItem;
import com.nonprofit.model.lookup.ResourceCategory;
import com.nonprofit.model.lookup.Skill;
import com.nonprofit.model.people.Beneficiary;
import com.nonprofit.model.people.Need;
import com.nonprofit.model.people.Volunteer;
import com.nonprofit.view.LookupView;

/**
 * MVC - Controller for the CRUD lookup tables (Skills, Item Categories,
 * Resource Categories). A row that is still in use cannot be deleted.
 */
public class LookupController {

    private static final int SKILLS = 1;
    private static final int ITEM_CATEGORIES = 2;
    private static final int RESOURCE_CATEGORIES = 3;

    private LookupView view;
    private DatabaseManager db;

    public LookupController(LookupView view) {
        this.view = view;
        this.db = DatabaseManager.getInstance();
    }

    public void run() {
        int tableChoice = view.show();
        while (tableChoice != 0) {
            GenericDAO<? extends LookupItem> table = getTable(tableChoice);
            if (table == null) {
                view.showError("Unknown option.");
            } else {
                manageTable(tableChoice, table);
            }
            tableChoice = view.show();
        }
    }

    private void manageTable(int tableChoice, GenericDAO<? extends LookupItem> table) {
        int choice = view.showTableMenu(table.getTableName());
        while (choice != 0) {
            switch (choice) {
                case 1 -> view.showLookupItems(table.findAll());
                case 2 -> create(tableChoice);
                case 3 -> update(table);
                case 4 -> delete(table);
                default -> view.showError("Unknown option.");
            }
            choice = view.showTableMenu(table.getTableName());
        }
    }

    // Create: each table builds its own kind of row
    private void create(int tableChoice) {
        String name = view.readText("Name: ");
        String description = view.readText("Description: ");
        if (name.isBlank()) {
            view.showError("Name is required.");
            return;
        }
        switch (tableChoice) {
            case SKILLS -> db.getSkills().insert(new Skill(db.nextId(), name, description, view.readYesNo("Requires training?")));
            case ITEM_CATEGORIES -> db.getItemCategories().insert(new ItemCategory(db.nextId(), name, description, view.readYesNo("Perishable?")));
            case RESOURCE_CATEGORIES -> db.getResourceCategories().insert(new ResourceCategory(db.nextId(), name, description, view.readYesNo("Essential?")));
            default -> view.showError("Unknown table.");
        }
        view.showMessage("Row added.");
    }

    // Update
    private void update(GenericDAO<? extends LookupItem> table) {
        view.showLookupItems(table.findAll());
        LookupItem item = table.findById(view.readInt("Id to edit: "));
        if (item == null) {
            view.showError("No row with that id.");
            return;
        }
        String name = view.readText("New name: ");
        if (name.isBlank()) {
            view.showError("Name is required.");
            return;
        }
        item.setName(name);
        item.setDescription(view.readText("New description: "));
        view.showMessage("Row updated.");
    }

    // Delete (refused while the row is referenced)
    private void delete(GenericDAO<? extends LookupItem> table) {
        view.showLookupItems(table.findAll());
        LookupItem item = table.findById(view.readInt("Id to delete: "));
        if (item == null) {
            view.showError("No row with that id.");
        } else if (isInUse(item)) {
            view.showError("'" + item.getName() + "' is in use and cannot be deleted.");
        } else {
            table.delete(item.getId());
            view.showMessage("Row deleted.");
        }
    }

    private boolean isInUse(LookupItem item) {
        for (Event event : db.getEvents().findAll()) {
            if (event.getRequiredSkill() == item) {
                return true;
            }
        }
        for (Volunteer volunteer : db.getVolunteers().findAll()) {
            if (volunteer.getSkills().contains(item)) {
                return true;
            }
        }
        for (Donation donation : db.getDonations().findAll()) {
            if (donation instanceof InKindDonation inKind && inKind.getCategory() == item) {
                return true;
            }
        }
        for (Resource resource : db.getResources().findAll()) {
            if (resource.getCategory() == item) {
                return true;
            }
        }
        for (Beneficiary beneficiary : db.getBeneficiaries().findAll()) {
            for (Need need : beneficiary.getNeeds()) {
                if (need.getCategory() == item) {
                    return true;
                }
            }
        }
        return false;
    }

    private GenericDAO<? extends LookupItem> getTable(int choice) {
        return switch (choice) {
            case SKILLS -> db.getSkills();
            case ITEM_CATEGORIES -> db.getItemCategories();
            case RESOURCE_CATEGORIES -> db.getResourceCategories();
            default -> null;
        };
    }
}
