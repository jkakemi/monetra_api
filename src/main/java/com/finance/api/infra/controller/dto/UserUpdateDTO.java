package com.finance.api.infra.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @NotNull
        @NotBlank
        String name,
        @NotNull
        @NotBlank
        String currPassword,
        @NotNull
        @NotBlank
        String newPassword
) {
}
