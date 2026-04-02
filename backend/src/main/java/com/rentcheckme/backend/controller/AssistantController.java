package com.rentcheckme.backend.controller;

import com.rentcheckme.backend.dto.AssistantQueryRequest;
import com.rentcheckme.backend.dto.AssistantResponse;
import com.rentcheckme.backend.service.AssistantService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/query")
    public AssistantResponse query(@RequestBody AssistantQueryRequest request) {
        return assistantService.answer(request);
    }
}
