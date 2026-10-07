package com.bhanureddy.expense_tracker.dto;

import java.math.BigDecimal;

public interface CategoryTotal {
    String getCategory();
    BigDecimal getTotal();
}