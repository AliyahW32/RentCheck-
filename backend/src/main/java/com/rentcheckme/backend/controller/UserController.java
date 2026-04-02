package com.rentcheckme.backend.controller;

import com.rentcheckme.backend.dto.UserSummaryResponse;
import com.rentcheckme.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserSummaryResponse> getUsers() {
        return userService.getUsers();
    }
}
