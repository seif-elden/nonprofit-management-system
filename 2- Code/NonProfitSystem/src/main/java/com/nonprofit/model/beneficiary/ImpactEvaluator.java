package com.nonprofit.model.beneficiary;

import java.util.List;

/**
 * STRATEGY PATTERN - Context: evaluates distributions with whichever
 * strategy is currently set.
 */
public class ImpactEvaluator {

    private ImpactEvaluationStrategy strategy;

    public void setStrategy(ImpactEvaluationStrategy strategy) {
        this.strategy = strategy;
    }

    public ImpactReport evaluate(List<Distribution> distributions) {
        return strategy.evaluate(distributions);
    }
}
