package com.rentcheckme.backend.model;

public class FinanceProfile {
    private double income;
    private double debt;
    private double savings;
    private double cash;
    private int roommates;

    public FinanceProfile() {
    }

    public FinanceProfile(double income, double debt, double savings, double cash, int roommates) {
        this.income = income;
        this.debt = debt;
        this.savings = savings;
        this.cash = cash;
        this.roommates = roommates;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public int getRoommates() {
        return roommates;
    }

    public void setRoommates(int roommates) {
        this.roommates = roommates;
    }
}
