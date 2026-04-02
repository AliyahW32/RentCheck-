package com.rentcheckme.backend.dto;

import com.rentcheckme.backend.model.User;

public class UserSummaryResponse {
    private String id;
    private String name;
    private String role;
    private String city;

    public UserSummaryResponse() {
    }

    public UserSummaryResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.role = user.getRole().name().toLowerCase();
        this.city = user.getCity();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getCity() {
        return city;
    }
}
