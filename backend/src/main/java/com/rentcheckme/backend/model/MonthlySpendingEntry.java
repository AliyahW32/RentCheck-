package com.rentcheckme.backend.model;

public class MonthlySpendingEntry {
    private String month;
    private String category;
    private String note;
    private double planned;
    private double actual;

    public MonthlySpendingEntry() {
    }

    public MonthlySpendingEntry(String month, String category, String note, double planned, double actual) {
        this.month = month;
        this.category = category;
        this.note = note;
        this.planned = planned;
        this.actual = actual;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getPlanned() {
        return planned;
    }

    public void setPlanned(double planned) {
        this.planned = planned;
    }

    public double getActual() {
        return actual;
    }

    public void setActual(double actual) {
        this.actual = actual;
    }
}
