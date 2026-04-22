package com.rentcheckme.backend.dto;

import java.util.List;

public class SpendingSheetRequest {
    private List<MonthlySpendingEntryRequest> entries;

    public SpendingSheetRequest() {
    }

    public List<MonthlySpendingEntryRequest> getEntries() {
        return entries;
    }

    public void setEntries(List<MonthlySpendingEntryRequest> entries) {
        this.entries = entries;
    }
}
