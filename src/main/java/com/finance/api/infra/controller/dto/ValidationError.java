package com.finance.api.infra.controller.dto;

import java.time.Instant;
import java.util.List;

public record ValidationError(
        String field,
        String message
) {
    public ValidationError(org.springframework.validation.FieldError erro) {
        this(erro.getField(), erro.getDefaultMessage());
    }

}
