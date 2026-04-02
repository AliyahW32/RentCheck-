package com.rentcheckme.backend.model;

public class User {
    private String id;
    private String name;
    private UserRole role;
    private String city;
    private FinanceProfile finances;
    private UserPreferences preferences;

    public User() {
    }

    public User(String id, String name, UserRole role, String city, FinanceProfile finances, UserPreferences preferences) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.city = city;
        this.finances = finances;
        this.preferences = preferences;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public FinanceProfile getFinances() {
        return finances;
    }

    public void setFinances(FinanceProfile finances) {
        this.finances = finances;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }
}
