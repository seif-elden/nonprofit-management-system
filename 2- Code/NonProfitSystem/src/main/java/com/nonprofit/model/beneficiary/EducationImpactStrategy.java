package com.nonprofit.model.beneficiary;

import java.util.List;

/**
 * STRATEGY PATTERN - Concrete Strategy: education is measured by how
 * many distributions went to beneficiaries who actually asked for that
 * kind of support (need-match rate).
 */
public class EducationImpactStrategy implements ImpactEvaluationStrategy {

    @Override
    public ImpactReport evaluate(List<Distribution> distributions) {
        int matched = 0;
        for (Distribution distribution : distributions) {
            if (distribution.getBeneficiary().hasNeed(distribution.getResource().getCategory())) {
                matched++;
            }
        }
        double rate = distributions.isEmpty() ? 0 : matched * 100.0 / distributions.size();
        return new ImpactReport("Education", rate, String.format(
                "%d of %d distributions matched a recorded need: %.0f%% need-match rate.", matched, distributions.size(), rate));
    }
}
