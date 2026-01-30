package com.finance.api.domain.user;

public class Email {
    private String address;

    public Email(String address) {
        if (address == null || !address.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email address. Should be: example@example.com");
        }
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
