package com.rentcheckme.backend.model;

import java.util.List;

public class UserPreferences {
    private String beds;
    private int maxCommute;
    private List<String> priorities;
    private String useCase;
    private String budgetingFor;

    public UserPreferences() {
    }

    public UserPreferences(String beds, int maxCommute, List<String> priorities, String useCase, String budgetingFor) {
        this.beds = beds;
        this.maxCommute = maxCommute;
        this.priorities = priorities;
        this.useCase = useCase;
        this.budgetingFor = budgetingFor;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public int getMaxCommute() {
        return maxCommute;
    }

    public void setMaxCommute(int maxCommute) {
        this.maxCommute = maxCommute;
    }

    public List<String> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<String> priorities) {
        this.priorities = priorities;
    }

    public String getUseCase() {
        return useCase;
    }

    public void setUseCase(String useCase) {
        this.useCase = useCase;
    }

    public String getBudgetingFor() {
        return budgetingFor;
    }

    public void setBudgetingFor(String budgetingFor) {
        this.budgetingFor = budgetingFor;
    }
}
