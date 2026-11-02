package com.nonprofit.model.beneficiary;

/**
 * Result of evaluating a program's impact.
 */
public class ImpactReport {

    private String programName;
    private double score;
    private String summary;

    public ImpactReport(String programName, double score, String summary) {
        this.programName = programName;
        this.score = score;
        this.summary = summary;
    }

    public String getProgramName() {
        return programName;
    }

    public double getScore() {
        return score;
    }

    public String getSummary() {
        return summary;
    }
}
