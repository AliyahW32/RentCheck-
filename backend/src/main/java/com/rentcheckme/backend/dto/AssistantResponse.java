package com.rentcheckme.backend.dto;

public class AssistantResponse {
    private boolean allowed;
    private String reply;

    public AssistantResponse() {
    }

    public AssistantResponse(boolean allowed, String reply) {
        this.allowed = allowed;
        this.reply = reply;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getReply() {
        return reply;
    }
}
