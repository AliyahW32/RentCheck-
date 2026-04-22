package com.rentcheckme.backend.service;

import com.rentcheckme.backend.dto.AuthRequest;
import com.rentcheckme.backend.dto.AuthResponse;
import com.rentcheckme.backend.dto.OnboardingRequest;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    public AuthService(UserService userService, UserRepository userRepository, PasswordService passwordService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public AuthResponse register(OnboardingRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with that email already exists.");
        }

        User user = userService.createUser(request);
        return new AuthResponse(createSession(user.getId()), user);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));

        if (!passwordService.matches(request.getPassword(), user.getPasswordProfile())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        return new AuthResponse(createSession(user.getId()), user);
    }

    public User requireUser(String authorizationHeader, String userId) {
        String token = extractBearerToken(authorizationHeader);
        String sessionUserId = sessions.get(token);

        if (sessionUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        if (userId != null && !userId.isBlank() && !sessionUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token does not match the requested user.");
        }

        return userService.getUserOrDefault(sessionUserId);
    }

    public String createSession(String userId) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, userId);
        return token;
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token.");
        }

        return authorizationHeader.substring("Bearer ".length()).trim();
    }
}
