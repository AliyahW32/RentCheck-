package com.rentcheckme.backend.dto;

public class BudgetResponse {
    private double monthlyExpenses;
    private double afterObligations;
    private double incomeCap;
    private double housingBudget;
    private double vehicleBudget;
    private double moneyToWorkWith;

    public BudgetResponse() {
    }

    public BudgetResponse(double monthlyExpenses, double afterObligations, double incomeCap,
                          double housingBudget, double vehicleBudget, double moneyToWorkWith) {
        this.monthlyExpenses = monthlyExpenses;
        this.afterObligations = afterObligations;
        this.incomeCap = incomeCap;
        this.housingBudget = housingBudget;
        this.vehicleBudget = vehicleBudget;
        this.moneyToWorkWith = moneyToWorkWith;
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

    public double getVehicleBudget() {
        return vehicleBudget;
    }

    public double getMoneyToWorkWith() {
        return moneyToWorkWith;
    }
}
