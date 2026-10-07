package com.bhanureddy.expense_tracker.controller;

import com.bhanureddy.expense_tracker.dto.ExpenseRequest;
import com.bhanureddy.expense_tracker.dto.ExpenseResponse;
import com.bhanureddy.expense_tracker.model.Expense;
import com.bhanureddy.expense_tracker.model.User;
import com.bhanureddy.expense_tracker.repository.ExpenseRepository;
import com.bhanureddy.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock ExpenseRepository expenseRepository;
    @Mock UserRepository userRepository;
    @Mock Authentication auth;
    @InjectMocks ExpenseController controller;

    private final ExpenseRequest request =
            new ExpenseRequest(new BigDecimal("99.00"), "Travel", LocalDate.of(2026, 10, 6), "Bus");

    @Test
    void update_otherUsersExpense_returns404() {
        User me = User.builder().id(1L).email("me@x.com").build();
        User other = User.builder().id(2L).email("other@x.com").build();
        Expense expense = Expense.builder().id(10L).user(other).build();

        when(auth.getName()).thenReturn("me@x.com");
        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(me));
        when(expenseRepository.findById(10L)).thenReturn(Optional.of(expense));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.update(auth, 10L, request));

        assertEquals(404, ex.getStatusCode().value());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void update_ownExpense_savesChanges() {
        User me = User.builder().id(1L).email("me@x.com").build();
        Expense expense = Expense.builder().id(10L).user(me)
                .amount(new BigDecimal("5.00")).category("Food")
                .expenseDate(LocalDate.of(2026, 10, 1)).build();

        when(auth.getName()).thenReturn("me@x.com");
        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(me));
        when(expenseRepository.findById(10L)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(i -> i.getArgument(0));

        ExpenseResponse res = controller.update(auth, 10L, request);

        assertEquals(new BigDecimal("99.00"), res.amount());
        assertEquals("Travel", res.category());
    }
}