package com.nonprofit.model.beneficiary;

import com.nonprofit.model.persistence.DatabaseManager;
import com.nonprofit.model.lookup.ResourceCategory;
import com.nonprofit.model.observer.ChangeEvent;
import com.nonprofit.model.observer.ChangeType;
import com.nonprofit.model.observer.Observer;
import com.nonprofit.model.observer.Subject;
import com.nonprofit.model.people.Beneficiary;
import java.util.ArrayList;
import java.util.List;

/**
 * Beneficiary support logic: distributions and impact evaluation.
 * OBSERVER PATTERN - Concrete Subject: each distribution is published
 * (the stock observer reduces the stock, the activity log records it).
 */
public class DistributionService implements Subject {

    private List<Observer> observers;
    private ImpactEvaluator evaluator;

    public DistributionService() {
        observers = new ArrayList<>();
        evaluator = new ImpactEvaluator();
    }

    @Override
    public void addObserver(Observer o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(ChangeEvent change) {
        for (Observer observer : observers) {
            observer.update(this, change);
        }
    }

    /** Returns null when there is not enough stock. */
    public Distribution recordDistribution(Beneficiary beneficiary, Resource resource, int quantity) {
        if (!resource.hasStock(quantity)) {
            return null;
        }
        DatabaseManager db = DatabaseManager.getInstance();
        Distribution distribution = new Distribution(db.nextId(), beneficiary, resource, quantity);
        db.getDistributions().insert(distribution);
        notifyObservers(new ChangeEvent(ChangeType.DISTRIBUTION_RECORDED, distribution,
                quantity + " " + resource.getUnit() + "(s) of " + resource.getName() + " given to " + beneficiary.getName()));
        return distribution;
    }

    public List<Distribution> getDistributionsByBeneficiary(Beneficiary beneficiary) {
        List<Distribution> result = new ArrayList<>();
        for (Distribution distribution : DatabaseManager.getInstance().getDistributions().findAll()) {
            if (distribution.getBeneficiary() == beneficiary) {
                result.add(distribution);
            }
        }
        return result;
    }

    /** Strategy: the client picks how the program is measured; only the program type's distributions are scored. */
    public ImpactReport evaluateImpact(ResourceCategory programType, ImpactEvaluationStrategy strategy) {
        List<Distribution> selected = new ArrayList<>();
        for (Distribution distribution : DatabaseManager.getInstance().getDistributions().findAll()) {
            if (distribution.getResource().getCategory() == programType) {
                selected.add(distribution);
            }
        }
        evaluator.setStrategy(strategy);
        return evaluator.evaluate(selected);
    }
}
