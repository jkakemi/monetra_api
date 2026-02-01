package com.finance.api.infra.controller.dto;

public record UserRequestDTO(
        String name,
        String email,
        String cpf,
        String password) {
}
