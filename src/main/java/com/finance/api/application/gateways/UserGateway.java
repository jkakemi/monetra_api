package com.finance.api.application.gateways;

import com.finance.api.domain.user.User;

public interface UserGateway {
    User createUser(User user);
    //List<User> listAll();
    User findByEmail(String email);
    User updateUser(User user);
    void deleteUser(String email);
    boolean existsByEmail(String email);

}
