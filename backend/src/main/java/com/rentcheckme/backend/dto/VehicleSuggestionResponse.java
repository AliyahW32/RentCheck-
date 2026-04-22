package com.rentcheckme.backend.dto;

import java.util.List;

public class VehicleSuggestionResponse {
    private String desiredType;
    private String guidance;
    private List<VehicleOptionResponse> suggestions;

    public VehicleSuggestionResponse() {
    }

    public VehicleSuggestionResponse(String desiredType, String guidance, List<VehicleOptionResponse> suggestions) {
        this.desiredType = desiredType;
        this.guidance = guidance;
        this.suggestions = suggestions;
    }

    public String getDesiredType() {
        return desiredType;
    }

    public String getGuidance() {
        return guidance;
    }

    public List<VehicleOptionResponse> getSuggestions() {
        return suggestions;
    }
}
