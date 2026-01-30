package com.finance.api.infra.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountUpdateDTO (
        @NotNull
        @NotBlank
        String name,
        @NotNull
        BigDecimal creditLimit
){
}
