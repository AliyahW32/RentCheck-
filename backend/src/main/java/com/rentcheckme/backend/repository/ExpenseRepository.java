package com.rentcheckme.backend.repository;

import com.rentcheckme.backend.model.Expense;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenseRepository {

    private final List<Expense> expenses = List.of(
        new Expense("Transportation", "Car payment", 315),
        new Expense("Transportation", "Gas + maintenance", 165),
        new Expense("Food", "Groceries", 420),
        new Expense("Health", "Prescriptions", 55),
        new Expense("Utilities", "Phone", 85),
        new Expense("Lifestyle", "Subscriptions", 42),
        new Expense("Safety", "Emergency buffer", 200)
    );

    public List<Expense> findAll() {
        return expenses;
    }
}
