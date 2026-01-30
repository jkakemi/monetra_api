package com.finance.api.application.usecases.user;

import com.finance.api.application.gateways.UserGateway;

public class DeleteUser {
    private final UserGateway gateway;

    public DeleteUser(UserGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(String email) {
        gateway.deleteUser(email);
    }
}
