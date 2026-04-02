package com.rentcheckme.backend.model;

public class Expense {
    private String category;
    private String name;
    private double amount;

    public Expense() {
    }

    public Expense(String category, String name, double amount) {
        this.category = category;
        this.name = name;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
