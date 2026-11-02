package com.nonprofit.model.beneficiary;

import java.util.List;

/**
 * STRATEGY PATTERN - Strategy interface.
 * Each kind of program measures its impact differently.
 */
public interface ImpactEvaluationStrategy {

    ImpactReport evaluate(List<Distribution> distributions);
}
