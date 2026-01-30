package com.finance.api.infra.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequestDTO(
        @NotBlank
        @NotNull
        String name,
        @NotNull
        @NotBlank
        String type) {}