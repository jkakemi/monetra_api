package com.finance.api.application.usecases.user;

import com.finance.api.application.gateways.PasswordEncoderGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.user.User;

public class LoginUser {
    private final UserGateway userGateway;
    private final PasswordEncoderGateway passwordEncoder;

    public LoginUser(UserGateway userGateway, PasswordEncoderGateway passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(String email, String password) {
        User user = userGateway.findByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user;
    }
}
