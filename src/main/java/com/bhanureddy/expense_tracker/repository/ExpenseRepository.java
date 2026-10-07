package com.bhanureddy.expense_tracker.repository;

import com.bhanureddy.expense_tracker.dto.CategoryTotal;
import com.bhanureddy.expense_tracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate from, LocalDate to);

    @Query("""
            select e.category as category, sum(e.amount) as total
            from Expense e
            where e.user.id = :userId and e.expenseDate between :from and :to
            group by e.category
            """)
    List<CategoryTotal> summarize(@Param("userId") Long userId,
                                  @Param("from") LocalDate from,
                                  @Param("to") LocalDate to);
}