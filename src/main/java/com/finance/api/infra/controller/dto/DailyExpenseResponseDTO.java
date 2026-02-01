package com.finance.api.infra.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyExpenseResponseDTO(
        LocalDate date,
        BigDecimal total
) {}
