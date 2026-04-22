package com.rentcheckme.backend.controller;

import com.rentcheckme.backend.dto.OnboardingRequest;
import com.rentcheckme.backend.dto.UserSummaryResponse;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.service.AuthService;
import com.rentcheckme.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public List<UserSummaryResponse> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId, @RequestHeader("Authorization") String authorization) {
        authService.requireUser(authorization, userId);
        return userService.getUserProfile(userId);
    }

    @PostMapping("/onboarding")
    public User createUser(@RequestBody OnboardingRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{userId}/onboarding")
    public User updateUser(@PathVariable String userId,
                           @RequestHeader("Authorization") String authorization,
                           @RequestBody OnboardingRequest request) {
        authService.requireUser(authorization, userId);
        return userService.updateUser(userId, request);
    }
}
