package com.rentcheckme.backend.dto;

import com.rentcheckme.backend.model.Expense;
import com.rentcheckme.backend.model.FinanceProfile;

import java.util.List;

public class DashboardRequest {
    private String userId;
    private String city;
    private List<String> areaIds;
    private FinanceProfile finances;
    private List<Expense> expenses;

    public DashboardRequest() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(List<String> areaIds) {
        this.areaIds = areaIds;
    }

    public FinanceProfile getFinances() {
        return finances;
    }

    public void setFinances(FinanceProfile finances) {
        this.finances = finances;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}
