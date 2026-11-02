package com.nonprofit.model.observer;

import com.nonprofit.model.beneficiary.Distribution;

/**
 * OBSERVER PATTERN - Concrete Observer: reduces the resource stock
 * every time a distribution is recorded.
 */
public class StockObserver implements Observer {

    public StockObserver(Subject distributionService) {
        distributionService.addObserver(this);
    }

    @Override
    public void update(Subject source, ChangeEvent change) {
        if (change.getType() == ChangeType.DISTRIBUTION_RECORDED) {
            Distribution distribution = (Distribution) change.getPayload();
            distribution.getResource().reduceStock(distribution.getQuantity());
        }
    }
}
