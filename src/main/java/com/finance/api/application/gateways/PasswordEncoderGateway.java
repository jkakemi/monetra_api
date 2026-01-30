package com.finance.api.application.gateways;

public interface PasswordEncoderGateway {
    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
}
