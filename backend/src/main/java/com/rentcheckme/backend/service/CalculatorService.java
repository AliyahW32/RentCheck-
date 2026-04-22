package com.rentcheckme.backend.service;

import com.rentcheckme.backend.dto.BudgetResponse;
import com.rentcheckme.backend.dto.AnalyticsResponse;
import com.rentcheckme.backend.dto.MonthlySpendingSummary;
import com.rentcheckme.backend.dto.SpendingBreakdownItem;
import com.rentcheckme.backend.dto.VehicleSuggestionResponse;
import com.rentcheckme.backend.dto.VehicleOptionResponse;
import com.rentcheckme.backend.model.Expense;
import com.rentcheckme.backend.model.FinanceProfile;
import com.rentcheckme.backend.model.MonthlySpendingEntry;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalculatorService {

    public BudgetResponse calculateBudget(FinanceProfile finances, List<Expense> expenses) {
        double baselineExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double monthlyExpenses = finances.getMonthlyEssentials() > 0 ? finances.getMonthlyEssentials() : baselineExpenses;
        double afterObligations = finances.getIncome() - monthlyExpenses - finances.getDebt() - finances.getSavings();
        double incomeCap = finances.getIncome() * 0.35;
        double housingBudget = Math.max(0, Math.min(afterObligations, incomeCap));
        double moneyToWorkWith = Math.max(0, afterObligations - housingBudget);
        double vehicleBudget = Math.max(0, Math.min(afterObligations * 0.45, 850));
        return new BudgetResponse(monthlyExpenses, afterObligations, incomeCap, housingBudget, vehicleBudget, moneyToWorkWith);
    }

    public AnalyticsResponse calculateAnalytics(FinanceProfile finances, BudgetResponse budget, List<MonthlySpendingEntry> entries) {
        double committedSpending = budget.getMonthlyExpenses() + finances.getDebt() + budget.getHousingBudget();
        List<MonthlySpendingEntry> safeEntries = entries == null ? List.of() : entries;
        List<MonthlySpendingSummary> monthlySummaries = summarizeMonths(safeEntries);
        return new AnalyticsResponse(
            finances.getIncome(),
            committedSpending,
            finances.getSavings(),
            budget.getMoneyToWorkWith(),
            budget.getHousingBudget(),
            budget.getVehicleBudget(),
            List.of(
                new SpendingBreakdownItem("Housing target", "Recommended monthly rent and housing stack.", budget.getHousingBudget()),
                new SpendingBreakdownItem("Debt obligations", "Recurring debt already spoken for.", finances.getDebt()),
                new SpendingBreakdownItem("Savings goal", "Planned monthly amount reserved for savings.", finances.getSavings()),
                new SpendingBreakdownItem("Essential spending", "Core living costs before housing choices.", budget.getMonthlyExpenses()),
                new SpendingBreakdownItem("Money to work with", "Remaining flexible cash after the planned budget.", budget.getMoneyToWorkWith())
            ),
            safeEntries,
            monthlySummaries,
            buildFeedback(monthlySummaries)
        );
    }

    public List<VehicleOptionResponse> calculateVehicleOptions(BudgetResponse budget) {
        return List.of(
            new VehicleOptionResponse(
                "transit",
                "Transit-first",
                "Lowest transportation load when housing is the priority.",
                roundCurrency(budget.getVehicleBudget() * 0.35),
                300
            ),
            new VehicleOptionResponse(
                "used",
                "Used vehicle",
                "Balanced monthly payment target for flexibility without stretching the budget.",
                roundCurrency(budget.getVehicleBudget() * 0.7),
                2200
            ),
            new VehicleOptionResponse(
                "newer",
                "Newer vehicle",
                "Only realistic when budget pressure is still low after housing and savings.",
                roundCurrency(budget.getVehicleBudget()),
                4800
            )
        );
    }

    public VehicleSuggestionResponse suggestVehicles(BudgetResponse budget, String desiredType) {
        String normalized = desiredType == null || desiredType.isBlank() ? "general car" : desiredType.trim().toLowerCase();
        double multiplier = switch (normalized) {
            case "suv", "truck", "luxury" -> 1.0;
            case "sedan", "compact suv", "crossover", "electric", "hybrid" -> 0.86;
            case "used", "hatchback", "small car" -> 0.68;
            default -> 0.75;
        };

        List<VehicleOptionResponse> suggestions = List.of(
            new VehicleOptionResponse(
                normalized.replace(" ", "-") + "-starter",
                titleCase(normalized) + " starter path",
                "Start used or lower-trim first and keep the all-in car cost under control.",
                roundCurrency(budget.getVehicleBudget() * Math.min(0.78, multiplier)),
                roundCurrency(1800 + budget.getVehicleBudget() * 1.15)
            ),
            new VehicleOptionResponse(
                normalized.replace(" ", "-") + "-stretch",
                titleCase(normalized) + " stretch path",
                "Only take this route if housing, savings, and insurance still remain comfortable.",
                roundCurrency(budget.getVehicleBudget() * Math.min(0.95, Math.max(0.75, multiplier))),
                roundCurrency(2600 + budget.getVehicleBudget() * 1.55)
            )
        );

        String guidance = multiplier >= 0.95
            ? "This vehicle type is on the expensive side. Prioritize down payment, insurance estimates, and total monthly cost before committing."
            : "This vehicle type is more realistic if you keep the payment, insurance, fuel, and maintenance inside the suggested monthly range.";

        return new VehicleSuggestionResponse(titleCase(normalized), guidance, suggestions);
    }

    private double roundCurrency(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private List<MonthlySpendingSummary> summarizeMonths(List<MonthlySpendingEntry> entries) {
        return entries.stream()
            .collect(Collectors.groupingBy(MonthlySpendingEntry::getMonth))
            .entrySet()
            .stream()
            .map(entry -> {
                double planned = entry.getValue().stream().mapToDouble(MonthlySpendingEntry::getPlanned).sum();
                double actual = entry.getValue().stream().mapToDouble(MonthlySpendingEntry::getActual).sum();
                double variance = roundCurrency(actual - planned);
                String status = variance > 75 ? "Overspending" : variance < -75 ? "Under budget" : "On track";
                return new MonthlySpendingSummary(entry.getKey(), roundCurrency(planned), roundCurrency(actual), variance, status);
            })
            .sorted(Comparator.comparing(MonthlySpendingSummary::getMonth))
            .toList();
    }

    private String buildFeedback(List<MonthlySpendingSummary> summaries) {
        if (summaries.isEmpty()) {
            return "Add spending rows to start comparing months and tracking whether you are over budget.";
        }

        MonthlySpendingSummary latest = summaries.get(summaries.size() - 1);
        if (summaries.size() == 1) {
            return latest.getStatus().equals("Overspending")
                ? "This month is above plan so far. Cut back in the highest-variable categories first."
                : "This month is tracking well so far. Keep adding entries to build a stronger trend line.";
        }

        MonthlySpendingSummary previous = summaries.get(summaries.size() - 2);
        if (latest.getActualTotal() > previous.getActualTotal()) {
            return "Spending increased compared with last month. Review the categories with the largest jump before adding new fixed costs.";
        }
        if (latest.getActualTotal() < previous.getActualTotal()) {
            return "You are doing better than last month. The current month is trending in a healthier direction.";
        }
        return "Spending is nearly flat compared with last month. Keep watching recurring costs for small creep.";
    }

    private String titleCase(String value) {
        return Arrays.stream(String.valueOf(value).split(" "))
            .map(part -> part.isBlank() ? part : Character.toUpperCase(part.charAt(0)) + part.substring(1))
            .collect(Collectors.joining(" "));
    }
}
