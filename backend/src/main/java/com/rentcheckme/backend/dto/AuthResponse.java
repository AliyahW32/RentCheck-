package com.rentcheckme.backend.dto;

import com.rentcheckme.backend.model.User;

public class AuthResponse {
    private String token;
    private User user;

    public AuthResponse() {
    }

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
