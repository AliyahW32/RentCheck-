package com.rentcheckme.backend.dto;

public class MonthlySpendingSummary {
    private String month;
    private double plannedTotal;
    private double actualTotal;
    private double variance;
    private String status;

    public MonthlySpendingSummary() {
    }

    public MonthlySpendingSummary(String month, double plannedTotal, double actualTotal, double variance, String status) {
        this.month = month;
        this.plannedTotal = plannedTotal;
        this.actualTotal = actualTotal;
        this.variance = variance;
        this.status = status;
    }

    public String getMonth() {
        return month;
    }

    public double getPlannedTotal() {
        return plannedTotal;
    }

    public double getActualTotal() {
        return actualTotal;
    }

    public double getVariance() {
        return variance;
    }

    public String getStatus() {
        return status;
    }
}
