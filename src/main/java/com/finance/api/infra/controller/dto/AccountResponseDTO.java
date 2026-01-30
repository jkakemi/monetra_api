package com.finance.api.infra.controller.dto;

import java.math.BigDecimal;

public record AccountResponseDTO(
        Long id,
        String name,
        String type,
        String accountNumber,
        BigDecimal balance,
        BigDecimal creditLimit
) {
}
