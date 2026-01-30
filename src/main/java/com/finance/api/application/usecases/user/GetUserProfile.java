package com.finance.api.application.usecases.user;

import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.user.User;

public class GetUserProfile {
    private final UserGateway gateway;

    public GetUserProfile(UserGateway gateway) {
        this.gateway = gateway;
    }

    public User execute(String email) {
        return gateway.findByEmail(email);
    }
}
