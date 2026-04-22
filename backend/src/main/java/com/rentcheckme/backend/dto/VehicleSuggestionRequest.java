package com.rentcheckme.backend.dto;

public class VehicleSuggestionRequest {
    private String desiredType;

    public VehicleSuggestionRequest() {
    }

    public String getDesiredType() {
        return desiredType;
    }

    public void setDesiredType(String desiredType) {
        this.desiredType = desiredType;
    }
}
