package com.nonprofit.model.beneficiary;

import com.nonprofit.model.people.Beneficiary;
import java.util.ArrayList;
import java.util.List;

/**
 * STRATEGY PATTERN - Concrete Strategy: food aid is measured by the
 * number of people fed and the units given per person.
 */
public class FoodAidImpactStrategy implements ImpactEvaluationStrategy {

    @Override
    public ImpactReport evaluate(List<Distribution> distributions) {
        List<Beneficiary> reached = new ArrayList<>();
        int units = 0;
        int people = 0;
        for (Distribution distribution : distributions) {
            units += distribution.getQuantity();
            if (!reached.contains(distribution.getBeneficiary())) {
                reached.add(distribution.getBeneficiary());
                people += distribution.getBeneficiary().getFamilySize();
            }
        }
        double perPerson = people == 0 ? 0 : (double) units / people;
        return new ImpactReport("Food aid", perPerson, String.format(
                "%d household(s) (%d people) received %d units: %.2f units per person.", reached.size(), people, units, perPerson));
    }
}
