package com.finance.api.application.usecases.user;

import com.finance.api.application.gateways.PasswordEncoderGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.user.User;
import com.finance.api.domain.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUser {
    private final UserGateway userGateway;
    private final PasswordEncoderGateway encoder;
    private static final Logger logger = LoggerFactory.getLogger(CreateUser.class);

    public CreateUser(UserGateway userGateway, PasswordEncoderGateway encoder) {
        this.userGateway = userGateway;
        this.encoder = encoder;
    }

    public User execute(String name, String email, String password, String cpf) {
        if (userGateway.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password length less than 6 characters");
        }

        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF is required");
        }

        String encodedPassword = encoder.encode(password);

        logger.info("Creating user with name {} and email {} and cpf {}", name, email, cpf);
        User user = new User(
                cpf,
                name,
                email,
                encodedPassword,
                UserRole.USER
        );

        return userGateway.createUser(user);
    }
}
