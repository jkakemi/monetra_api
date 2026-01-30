package com.finance.api.domain.user;

public class Cpf {
    private String number;

    public Cpf(String number) {
        if (number == null || !number.matches("\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}")) {
            throw new IllegalArgumentException("Invalid CPF number. Should be: XXX.XXX.XXX-XX");
        }
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
