package com.rentcheckme.backend.dto;

public class BudgetResponse {
    private double monthlyExpenses;
    private double afterObligations;
    private double incomeCap;
    private double housingBudget;

    public BudgetResponse() {
    }

    public BudgetResponse(double monthlyExpenses, double afterObligations, double incomeCap, double housingBudget) {
        this.monthlyExpenses = monthlyExpenses;
        this.afterObligations = afterObligations;
        this.incomeCap = incomeCap;
        this.housingBudget = housingBudget;
    }

    public double getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public double getAfterObligations() {
        return afterObligations;
    }

    public double getIncomeCap() {
        return incomeCap;
    }

    public double getHousingBudget() {
        return housingBudget;
    }
}
