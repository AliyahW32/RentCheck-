package com.rentcheckme.backend.dto;

import com.rentcheckme.backend.model.CityMap;
import com.rentcheckme.backend.model.Expense;
import com.rentcheckme.backend.model.User;

import java.util.List;

public class DashboardResponse {
    private User user;
    private List<Expense> expenses;
    private CityMap cityMap;
    private BudgetResponse budget;
    private List<String> selectedAreaIds;
    private List<ListingView> listings;
    private List<String> recommendations;
    private AnalyticsResponse analytics;
    private List<VehicleOptionResponse> vehicleOptions;
    private List<String> supportedCities;

    public DashboardResponse() {
    }

    public DashboardResponse(User user, List<Expense> expenses, CityMap cityMap, BudgetResponse budget,
                             List<String> selectedAreaIds, List<ListingView> listings, List<String> recommendations,
                             AnalyticsResponse analytics, List<VehicleOptionResponse> vehicleOptions,
                             List<String> supportedCities) {
        this.user = user;
        this.expenses = expenses;
        this.cityMap = cityMap;
        this.budget = budget;
        this.selectedAreaIds = selectedAreaIds;
        this.listings = listings;
        this.recommendations = recommendations;
        this.analytics = analytics;
        this.vehicleOptions = vehicleOptions;
        this.supportedCities = supportedCities;
    }

    public User getUser() {
        return user;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public CityMap getCityMap() {
        return cityMap;
    }

    public BudgetResponse getBudget() {
        return budget;
    }

    public List<String> getSelectedAreaIds() {
        return selectedAreaIds;
    }

    public List<ListingView> getListings() {
        return listings;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public AnalyticsResponse getAnalytics() {
        return analytics;
    }

    public List<VehicleOptionResponse> getVehicleOptions() {
        return vehicleOptions;
    }

    public List<String> getSupportedCities() {
        return supportedCities;
    }
}
