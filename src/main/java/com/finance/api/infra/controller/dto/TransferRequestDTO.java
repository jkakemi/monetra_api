package com.finance.api.infra.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferRequestDTO(
        @NotNull
        Long sourceAccountId,
        @NotNull
        @NotBlank
        String targetAccountNumber,
        @NotNull
        @NotBlank
        String targetAccountType,
        @NotNull
        BigDecimal amount,
        @NotNull
        String description,
        @NotNull
        LocalDate date,
        @NotNull
        Long categoryId
) {}