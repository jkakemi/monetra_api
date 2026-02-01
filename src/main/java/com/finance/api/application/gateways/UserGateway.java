package com.finance.api.application.gateways;

import com.finance.api.domain.user.User;

import java.util.List;

public interface UserGateway {
    User createUser(User user);
    List<User> listAll();
    User findByEmail(String email);
    User updateUser(User user);
    void deleteUser(String email);
    boolean existsByEmail(String email);

    boolean existsByCpf(String number);
}
