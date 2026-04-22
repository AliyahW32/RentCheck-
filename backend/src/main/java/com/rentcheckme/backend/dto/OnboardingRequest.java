package com.rentcheckme.backend.dto;

import com.rentcheckme.backend.model.FinanceProfile;
import com.rentcheckme.backend.model.UserRole;

import java.util.List;

public class OnboardingRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private UserRole role;
    private String city;
    private String zipCode;
    private boolean useCurrentLocation;
    private String currentLocationLabel;
    private String useCase;
    private String budgetingFor;
    private String beds;
    private int maxCommute;
    private List<String> priorities;
    private FinanceProfile finances;

    public OnboardingRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean isUseCurrentLocation() {
        return useCurrentLocation;
    }

    public void setUseCurrentLocation(boolean useCurrentLocation) {
        this.useCurrentLocation = useCurrentLocation;
    }

    public String getCurrentLocationLabel() {
        return currentLocationLabel;
    }

    public void setCurrentLocationLabel(String currentLocationLabel) {
        this.currentLocationLabel = currentLocationLabel;
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

    public FinanceProfile getFinances() {
        return finances;
    }

    public void setFinances(FinanceProfile finances) {
        this.finances = finances;
    }
}
