package com.bhanureddy.expense_tracker.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive") BigDecimal amount,
        @NotBlank String category,
        @NotNull LocalDate expenseDate,
        String note) {
}