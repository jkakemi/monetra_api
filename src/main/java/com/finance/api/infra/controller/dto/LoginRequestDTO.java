package com.finance.api.infra.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO (
        @NotBlank
        @Email
        String email,
        @NotBlank
        @NotNull
        String password) {
}
