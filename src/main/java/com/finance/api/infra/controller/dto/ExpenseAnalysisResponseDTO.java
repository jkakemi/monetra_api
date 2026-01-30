package com.finance.api.infra.controller.dto;

import java.math.BigDecimal;

public record ExpenseAnalysisResponseDTO(
        String categoryName,
        BigDecimal total
) {
}
