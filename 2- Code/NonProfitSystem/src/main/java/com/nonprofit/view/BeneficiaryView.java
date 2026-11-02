package com.nonprofit.view;

import com.nonprofit.model.beneficiary.Distribution;
import com.nonprofit.model.beneficiary.ImpactReport;
import com.nonprofit.model.beneficiary.Resource;
import com.nonprofit.model.people.Beneficiary;
import com.nonprofit.model.people.Need;
import java.util.List;

/**
 * MVC - View for beneficiaries, resources, distributions and impact.
 */
public class BeneficiaryView extends View {

    @Override
    public int show() {
        showTitle("Beneficiaries");
        showMessage("1. List beneficiaries and needs");
        showMessage("2. Add a need");
        showMessage("3. Record a distribution");
        showMessage("4. Distributions of a beneficiary");
        showMessage("5. Resources and stock");
        showMessage("6. Restock a resource");
        showMessage("7. Evaluate program impact");
        showMessage("0. Back");
        return readInt("Choose: ");
    }

    public int askProgram() {
        showMessage("Evaluate as: 1. Food aid program   2. Education program");
        return readInt("Choose: ");
    }

    public void showBeneficiaries(List<Beneficiary> beneficiaries) {
        if (beneficiaries.isEmpty()) {
            showMessage("(no beneficiaries)");
        }
        for (Beneficiary b : beneficiaries) {
            System.out.printf("  %-4d %-18s family of %d, %s%n", b.getId(), b.getName(), b.getFamilySize(), b.getAddress());
            for (Need need : b.getNeeds()) {
                System.out.printf("         need p%d: %s - %s%n", need.getPriority(), need.getCategory().getName(), need.getDescription());
            }
        }
    }

    public void showResources(List<Resource> resources) {
        for (Resource r : resources) {
            System.out.printf("  %-4d %-15s %-12s stock %d %s(s)%n", r.getId(), r.getName(), r.getCategory().getName(),
                    r.getStockQuantity(), r.getUnit());
        }
    }

    public void showDistributions(List<Distribution> distributions) {
        if (distributions.isEmpty()) {
            showMessage("(no distributions)");
        }
        for (Distribution d : distributions) {
            System.out.printf("  %-4d %s  %-18s %d x %s%n", d.getId(), d.getDate(), d.getBeneficiary().getName(),
                    d.getQuantity(), d.getResource().getName());
        }
    }

    public void showImpact(ImpactReport report) {
        showTitle("Impact: " + report.getProgramName());
        showMessage(String.format("  Score  : %.2f", report.getScore()));
        showMessage("  Summary: " + report.getSummary());
    }
}
