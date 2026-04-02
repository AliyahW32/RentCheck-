package com.rentcheckme.backend.service;

import com.rentcheckme.backend.dto.BudgetResponse;
import com.rentcheckme.backend.dto.DashboardRequest;
import com.rentcheckme.backend.dto.DashboardResponse;
import com.rentcheckme.backend.model.Expense;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final UserService userService;
    private final ExpenseRepository expenseRepository;
    private final CalculatorService calculatorService;
    private final ListingService listingService;
    private final MapService mapService;

    public DashboardService(UserService userService, ExpenseRepository expenseRepository,
                            CalculatorService calculatorService, ListingService listingService,
                            MapService mapService) {
        this.userService = userService;
        this.expenseRepository = expenseRepository;
        this.calculatorService = calculatorService;
        this.listingService = listingService;
        this.mapService = mapService;
    }

    public DashboardResponse getDashboard(String userId, List<String> areaIds) {
        DashboardRequest request = new DashboardRequest();
        request.setUserId(userId);
        request.setAreaIds(areaIds);
        return buildDashboard(request);
    }

    public DashboardResponse buildDashboard(DashboardRequest request) {
        User baseUser = userService.getUserOrDefault(request.getUserId());
        User user = userService.mergeUserOverrides(baseUser, request.getCity(), request.getFinances());
        List<Expense> expenses = request.getExpenses() == null || request.getExpenses().isEmpty()
            ? expenseRepository.findAll()
            : request.getExpenses();
        List<String> areaIds = request.getAreaIds() == null ? List.of() : request.getAreaIds();
        BudgetResponse budget = calculatorService.calculateBudget(user.getFinances(), expenses);

        return new DashboardResponse(
            user,
            expenses,
            mapService.getCityMap(user.getCity()),
            budget,
            areaIds,
            listingService.getListingsForUser(user, budget, areaIds),
            recommendationsFor(user)
        );
    }

    private List<String> recommendationsFor(User user) {
        return switch (user.getRole()) {
            case AGENT -> List.of(
                "Track neighborhoods where hidden costs push listings out of budget even when rent looks acceptable.",
                "Use the map to narrow inventory before discussing specific blocks with a renter."
            );
            case ADMIN -> List.of(
                "Monitor which areas have no realistic listings so the team can flag coverage gaps.",
                "Review move-in cash friction separately from monthly affordability."
            );
            case RENTER -> List.of(
                "Start broad by city, then click the map to keep only neighborhoods you would actually live in.",
                "If move-in cash is the blocker, compare shared 2-bedroom options before stretching monthly rent."
            );
        };
    }
}
