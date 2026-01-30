package com.finance.api.infra.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserRequestDTO(
        @NotNull
        @NotBlank
        String name,
        @NotNull
        @NotBlank
        @Email
        String email,
        @NotNull
        @NotBlank
        String cpf,
        @NotNull
        @NotBlank
        String password) {
}
