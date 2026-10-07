package com.bhanureddy.expense_tracker.dto;

import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password
) {}