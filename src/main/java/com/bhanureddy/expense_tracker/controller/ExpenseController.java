package com.bhanureddy.expense_tracker.controller;

import com.bhanureddy.expense_tracker.dto.*;
import com.bhanureddy.expense_tracker.model.Expense;
import com.bhanureddy.expense_tracker.model.User;
import com.bhanureddy.expense_tracker.repository.ExpenseRepository;
import com.bhanureddy.expense_tracker.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<ExpenseResponse> list(Authentication auth,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        User user = currentUser(auth);
        List<Expense> expenses = (month == null)
                ? expenseRepository.findByUserIdOrderByExpenseDateDesc(user.getId())
                : expenseRepository.findByUserIdAndExpenseDateBetween(
                        user.getId(), month.atDay(1), month.atEndOfMonth());
        return expenses.stream().map(ExpenseResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(Authentication auth, @Valid @RequestBody ExpenseRequest req) {
        Expense expense = Expense.builder()
                .amount(req.amount())
                .category(req.category())
                .expenseDate(req.expenseDate())
                .note(req.note())
                .user(currentUser(auth))
                .build();
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(Authentication auth, @PathVariable Long id,
                                  @Valid @RequestBody ExpenseRequest req) {
        Expense expense = findOwned(auth, id);
        expense.setAmount(req.amount());
        expense.setCategory(req.category());
        expense.setExpenseDate(req.expenseDate());
        expense.setNote(req.note());
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication auth, @PathVariable Long id) {
        expenseRepository.delete(findOwned(auth, id));
    }
    @GetMapping("/summary")
    public List<CategoryTotal> summary(Authentication auth,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        User user = currentUser(auth);
        return expenseRepository.summarize(user.getId(), month.atDay(1), month.atEndOfMonth());
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Expense findOwned(Authentication auth, Long id) {
        User user = currentUser(auth);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        if (!expense.getUser().getId().equals(user.getId())) {
            // 404, not 403, so we don't reveal that someone else's expense exists
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");
        }
        return expense;
    }
}