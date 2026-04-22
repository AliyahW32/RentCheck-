package com.rentcheckme.backend.dto;

public class SpendingBreakdownItem {
    private String label;
    private String description;
    private double amount;

    public SpendingBreakdownItem() {
    }

    public SpendingBreakdownItem(String label, String description, double amount) {
        this.label = label;
        this.description = description;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }
}
