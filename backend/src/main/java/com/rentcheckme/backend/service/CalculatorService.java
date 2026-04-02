package com.rentcheckme.backend.service;

import com.rentcheckme.backend.dto.BudgetResponse;
import com.rentcheckme.backend.model.Expense;
import com.rentcheckme.backend.model.FinanceProfile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculatorService {

    public BudgetResponse calculateBudget(FinanceProfile finances, List<Expense> expenses) {
        double monthlyExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double afterObligations = finances.getIncome() - monthlyExpenses - finances.getDebt() - finances.getSavings();
        double incomeCap = finances.getIncome() * 0.35;
        double housingBudget = Math.max(0, Math.min(afterObligations, incomeCap));
        return new BudgetResponse(monthlyExpenses, afterObligations, incomeCap, housingBudget);
    }
}
