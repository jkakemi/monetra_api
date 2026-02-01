package com.finance.api.infra.controller.dto;

import java.math.BigDecimal;

public record TotalExpenseResponseDTO(
        BigDecimal totalExpenses,
        BigDecimal totalIncome,
        BigDecimal balance
) {}
