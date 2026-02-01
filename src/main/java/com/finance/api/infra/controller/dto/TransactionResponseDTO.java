package com.finance.api.infra.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponseDTO(
        Long id,
        Long accountId,
        Long categoryId,
        BigDecimal amount,
        String currency,
        String type,
        String status,
        String description,
        LocalDate date
) {}