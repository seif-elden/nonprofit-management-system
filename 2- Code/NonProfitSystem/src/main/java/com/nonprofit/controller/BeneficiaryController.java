package com.nonprofit.controller;

import com.nonprofit.model.beneficiary.Distribution;
import com.nonprofit.model.beneficiary.DistributionService;
import com.nonprofit.model.beneficiary.EducationImpactStrategy;
import com.nonprofit.model.beneficiary.FoodAidImpactStrategy;
import com.nonprofit.model.beneficiary.ImpactEvaluationStrategy;
import com.nonprofit.model.beneficiary.Resource;
import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.lookup.ResourceCategory;
import com.nonprofit.model.people.Beneficiary;
import com.nonprofit.view.BeneficiaryView;

/**
 * MVC - Controller for beneficiaries: needs, distributions, stock and impact.
 */
public class BeneficiaryController {

    private BeneficiaryView view;
    private DistributionService distributionService;
    private DatabaseManager db;

    public BeneficiaryController(BeneficiaryView view, DistributionService distributionService) {
        this.view = view;
        this.distributionService = distributionService;
        this.db = DatabaseManager.getInstance();
    }

    public void run() {
        int choice = view.show();
        while (choice != 0) {
            switch (choice) {
                case 1 -> loadBeneficiaries();
                case 2 -> addNeed();
                case 3 -> recordDistribution();
                case 4 -> showDistributions();
                case 5 -> view.showResources(db.getResources().findAll());
                case 6 -> restock();
                case 7 -> showImpact();
                default -> view.showError("Unknown option.");
            }
            choice = view.show();
        }
    }

    private void loadBeneficiaries() {
        view.showBeneficiaries(db.getBeneficiaries().findAll());
    }

    private void addNeed() {
        Beneficiary beneficiary = chooseBeneficiary();
        if (beneficiary == null) {
            return;
        }
        view.showLookupItems(db.getResourceCategories().findAll());
        ResourceCategory category = db.getResourceCategories().findById(view.readInt("Category id: "));
        if (category == null) {
            view.showError("No category with that id.");
            return;
        }
        int priority = Math.max(1, Math.min(5, view.readInt("Priority (1 = most urgent, 5 = least): ")));
        beneficiary.addNeed(db.nextId(), category, view.readText("Description: "), priority);
        view.showMessage("Need added.");
    }

    private void recordDistribution() {
        Beneficiary beneficiary = chooseBeneficiary();
        if (beneficiary == null) {
            return;
        }
        view.showResources(db.getResources().findAll());
        Resource resource = db.getResources().findById(view.readInt("Resource id: "));
        if (resource == null) {
            view.showError("No resource with that id.");
            return;
        }
        Distribution distribution = distributionService.recordDistribution(beneficiary, resource, view.readInt("Quantity: "));
        if (distribution == null) {
            view.showError("Not recorded: quantity must be above 0 and not more than the stock.");
        } else {
            view.showMessage("Recorded. " + resource.getName() + " stock is now " + resource.getStockQuantity() + ".");
        }
    }

    private void showDistributions() {
        Beneficiary beneficiary = chooseBeneficiary();
        if (beneficiary != null) {
            view.showDistributions(distributionService.getDistributionsByBeneficiary(beneficiary));
        }
    }

    private void restock() {
        view.showResources(db.getResources().findAll());
        Resource resource = db.getResources().findById(view.readInt("Resource id: "));
        int quantity = resource == null ? 0 : view.readInt("Quantity to add: ");
        if (resource == null || quantity <= 0) {
            view.showError("Unknown resource or invalid quantity.");
            return;
        }
        resource.addStock(quantity);
        view.showMessage(resource.getName() + " stock is now " + resource.getStockQuantity() + ".");
    }

    /** Strategy: the user picks how the program is measured. */
    private void showImpact() {
        view.showLookupItems(db.getResourceCategories().findAll());
        ResourceCategory programType = db.getResourceCategories().findById(view.readInt("Program (resource category) id: "));
        if (programType == null) {
            view.showError("No category with that id.");
            return;
        }
        int program = view.askProgram();
        ImpactEvaluationStrategy strategy = program == 2 ? new EducationImpactStrategy() : new FoodAidImpactStrategy();
        view.showImpact(distributionService.evaluateImpact(programType, strategy));
    }

    private Beneficiary chooseBeneficiary() {
        loadBeneficiaries();
        Beneficiary beneficiary = db.getBeneficiaries().findById(view.readInt("Beneficiary id: "));
        if (beneficiary == null) {
            view.showError("No beneficiary with that id.");
        }
        return beneficiary;
    }
}
