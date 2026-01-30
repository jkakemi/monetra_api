package com.finance.api.application.usecases.user;

import com.finance.api.application.gateways.PasswordEncoderGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.user.User;

public class UpdateUser {
    private final UserGateway gateway;
    private final PasswordEncoderGateway passwordEncoder;

    public UpdateUser(UserGateway gateway, PasswordEncoderGateway passwordEncoder) {
        this.gateway = gateway;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(String email, String newName, String currentPassword, String newPassword) {
        User user = gateway.findByEmail(email);

        if (newName != null && !newName.isBlank()) {
            user.setName(newName);
        }

        if (newPassword != null && !newPassword.isBlank()) {

            if (newPassword.length() < 6) {
                throw new IllegalArgumentException("The new password must be longer than 6 characters");
            }
            if (currentPassword == null || currentPassword.isBlank()) {
                throw new IllegalArgumentException("To change your password, enter your current password");
            }

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new IllegalArgumentException("The curretn password doesn't match");
            }

            String encryptedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedNewPassword);
        }

        gateway.updateUser(user);
    }
}