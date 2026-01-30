package com.finance.api.infra.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountRequestDTO(
        @NotNull
        @NotBlank
        String name,
        @NotNull
        @NotBlank
        String type,
        @NotNull
        @NotBlank
        String accountNumber,
        @NotNull
        BigDecimal balance,
        @NotNull
        BigDecimal creditLimit
) {
}
