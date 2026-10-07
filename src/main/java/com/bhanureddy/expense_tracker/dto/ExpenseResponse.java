package com.bhanureddy.expense_tracker.dto;

import com.bhanureddy.expense_tracker.model.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Long id, BigDecimal amount, String category,
        LocalDate expenseDate, String note) {

    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(e.getId(), e.getAmount(), e.getCategory(),
                e.getExpenseDate(), e.getNote());
    }
}