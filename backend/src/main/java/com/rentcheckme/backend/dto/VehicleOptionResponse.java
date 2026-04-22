package com.rentcheckme.backend.dto;

public class VehicleOptionResponse {
    private String id;
    private String label;
    private String description;
    private double monthlyTarget;
    private double cashTarget;

    public VehicleOptionResponse() {
    }

    public VehicleOptionResponse(String id, String label, String description, double monthlyTarget, double cashTarget) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.monthlyTarget = monthlyTarget;
        this.cashTarget = cashTarget;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public double getMonthlyTarget() {
        return monthlyTarget;
    }

    public double getCashTarget() {
        return cashTarget;
    }
}
