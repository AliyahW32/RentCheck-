package com.rentcheckme.backend.controller;

import com.rentcheckme.backend.dto.AnalyticsResponse;
import com.rentcheckme.backend.dto.BudgetResponse;
import com.rentcheckme.backend.dto.SpendingSheetRequest;
import com.rentcheckme.backend.dto.VehicleSuggestionRequest;
import com.rentcheckme.backend.dto.VehicleSuggestionResponse;
import com.rentcheckme.backend.dto.VehicleOptionResponse;
import com.rentcheckme.backend.model.MonthlySpendingEntry;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.service.AuthService;
import com.rentcheckme.backend.repository.ExpenseRepository;
import com.rentcheckme.backend.service.CalculatorService;
import com.rentcheckme.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/planning")
public class PlanningController {

    private final UserService userService;
    private final ExpenseRepository expenseRepository;
    private final CalculatorService calculatorService;
    private final AuthService authService;

    public PlanningController(UserService userService, ExpenseRepository expenseRepository,
                              CalculatorService calculatorService, AuthService authService) {
        this.userService = userService;
        this.expenseRepository = expenseRepository;
        this.calculatorService = calculatorService;
        this.authService = authService;
    }

    @GetMapping("/{userId}/analytics")
    public AnalyticsResponse getAnalytics(@PathVariable String userId,
                                          @RequestHeader("Authorization") String authorization) {
        authService.requireUser(authorization, userId);
        User user = userService.getUserProfile(userId);
        BudgetResponse budget = calculatorService.calculateBudget(user.getFinances(), expenseRepository.findAll());
        return calculatorService.calculateAnalytics(user.getFinances(), budget, user.getMonthlySpendingEntries());
    }

    @GetMapping("/{userId}/vehicles")
    public List<VehicleOptionResponse> getVehicleOptions(@PathVariable String userId,
                                                         @RequestHeader("Authorization") String authorization) {
        authService.requireUser(authorization, userId);
        User user = userService.getUserProfile(userId);
        BudgetResponse budget = calculatorService.calculateBudget(user.getFinances(), expenseRepository.findAll());
        return calculatorService.calculateVehicleOptions(budget);
    }

    @PutMapping("/{userId}/analytics/spending")
    public AnalyticsResponse updateSpendingSheet(@PathVariable String userId,
                                                 @RequestHeader("Authorization") String authorization,
                                                 @RequestBody SpendingSheetRequest request) {
        authService.requireUser(authorization, userId);
        List<MonthlySpendingEntry> entries = request.getEntries() == null
            ? List.of()
            : request.getEntries().stream()
                .map(entry -> new MonthlySpendingEntry(
                    entry.getMonth(),
                    entry.getCategory(),
                    entry.getNote(),
                    entry.getPlanned(),
                    entry.getActual()
                ))
                .toList();
        User user = userService.updateMonthlySpendingEntries(userId, entries);
        BudgetResponse budget = calculatorService.calculateBudget(user.getFinances(), expenseRepository.findAll());
        return calculatorService.calculateAnalytics(user.getFinances(), budget, user.getMonthlySpendingEntries());
    }

    @PostMapping("/{userId}/vehicles/suggest")
    public VehicleSuggestionResponse suggestVehicle(@PathVariable String userId,
                                                    @RequestHeader("Authorization") String authorization,
                                                    @RequestBody VehicleSuggestionRequest request) {
        authService.requireUser(authorization, userId);
        User user = userService.getUserProfile(userId);
        BudgetResponse budget = calculatorService.calculateBudget(user.getFinances(), expenseRepository.findAll());
        return calculatorService.suggestVehicles(budget, request.getDesiredType());
    }
}
