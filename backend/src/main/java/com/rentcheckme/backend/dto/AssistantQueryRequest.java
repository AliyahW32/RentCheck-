package com.rentcheckme.backend.dto;

import java.util.List;

public class AssistantQueryRequest {
    private String userId;
    private String message;
    private List<String> areaIds;

    public AssistantQueryRequest() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(List<String> areaIds) {
        this.areaIds = areaIds;
    }
}
