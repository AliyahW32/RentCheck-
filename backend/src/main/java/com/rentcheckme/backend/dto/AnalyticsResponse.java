package com.rentcheckme.backend.dto;

import java.util.List;

public class AnalyticsResponse {
    private double income;
    private double committedSpending;
    private double savingsGoal;
    private double moneyToWorkWith;
    private double housingBudget;
    private double vehicleBudget;
    private List<SpendingBreakdownItem> breakdown;
    private List<com.rentcheckme.backend.model.MonthlySpendingEntry> spendingEntries;
    private List<MonthlySpendingSummary> monthlySummaries;
    private String feedback;

    public AnalyticsResponse() {
    }

    public AnalyticsResponse(double income, double committedSpending, double savingsGoal, double moneyToWorkWith,
                             double housingBudget, double vehicleBudget, List<SpendingBreakdownItem> breakdown,
                             List<com.rentcheckme.backend.model.MonthlySpendingEntry> spendingEntries,
                             List<MonthlySpendingSummary> monthlySummaries, String feedback) {
        this.income = income;
        this.committedSpending = committedSpending;
        this.savingsGoal = savingsGoal;
        this.moneyToWorkWith = moneyToWorkWith;
        this.housingBudget = housingBudget;
        this.vehicleBudget = vehicleBudget;
        this.breakdown = breakdown;
        this.spendingEntries = spendingEntries;
        this.monthlySummaries = monthlySummaries;
        this.feedback = feedback;
    }

    public double getIncome() {
        return income;
    }

    public double getCommittedSpending() {
        return committedSpending;
    }

    public double getSavingsGoal() {
        return savingsGoal;
    }

    public double getMoneyToWorkWith() {
        return moneyToWorkWith;
    }

    public double getHousingBudget() {
        return housingBudget;
    }

    public double getVehicleBudget() {
        return vehicleBudget;
    }

    public List<SpendingBreakdownItem> getBreakdown() {
        return breakdown;
    }

    public List<com.rentcheckme.backend.model.MonthlySpendingEntry> getSpendingEntries() {
        return spendingEntries;
    }

    public List<MonthlySpendingSummary> getMonthlySummaries() {
        return monthlySummaries;
    }

    public String getFeedback() {
        return feedback;
    }
}
