package com.finance.api.infra.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequestDTO(
        @NotNull
        Long accountId,
        @NotNull
        Long categoryId,
        @NotNull
        BigDecimal amount,
        @NotNull
        @NotBlank
        String currency,
        @NotNull
        @NotBlank
        String type,
        @NotNull
        @NotBlank
        String description,
        @NotNull
        LocalDate date
) {
}
