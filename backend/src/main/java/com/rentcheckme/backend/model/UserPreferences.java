package com.rentcheckme.backend.model;

import java.util.List;

public class UserPreferences {
    private String beds;
    private int maxCommute;
    private List<String> priorities;

    public UserPreferences() {
    }

    public UserPreferences(String beds, int maxCommute, List<String> priorities) {
        this.beds = beds;
        this.maxCommute = maxCommute;
        this.priorities = priorities;
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
}
