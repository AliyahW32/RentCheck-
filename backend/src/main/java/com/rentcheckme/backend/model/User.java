package com.rentcheckme.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private String city;
    private FinanceProfile finances;
    private UserPreferences preferences;
    private LocationPreference locationPreference;
    @JsonIgnore
    private PasswordProfile passwordProfile;
    private List<MonthlySpendingEntry> monthlySpendingEntries;

    public User() {
    }

    public User(String id, String name, String email, String phone, UserRole role, String city,
                FinanceProfile finances, UserPreferences preferences, LocationPreference locationPreference,
                PasswordProfile passwordProfile, List<MonthlySpendingEntry> monthlySpendingEntries) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.city = city;
        this.finances = finances;
        this.preferences = preferences;
        this.locationPreference = locationPreference;
        this.passwordProfile = passwordProfile;
        this.monthlySpendingEntries = monthlySpendingEntries;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public LocationPreference getLocationPreference() {
        return locationPreference;
    }

    public void setLocationPreference(LocationPreference locationPreference) {
        this.locationPreference = locationPreference;
    }

    public PasswordProfile getPasswordProfile() {
        return passwordProfile;
    }

    public void setPasswordProfile(PasswordProfile passwordProfile) {
        this.passwordProfile = passwordProfile;
    }

    public List<MonthlySpendingEntry> getMonthlySpendingEntries() {
        return monthlySpendingEntries;
    }

    public void setMonthlySpendingEntries(List<MonthlySpendingEntry> monthlySpendingEntries) {
        this.monthlySpendingEntries = monthlySpendingEntries;
    }
}
