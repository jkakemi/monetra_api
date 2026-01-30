package com.finance.api.infra.gateways;

import com.finance.api.domain.user.User;
import com.finance.api.domain.user.UserRole;
import com.finance.api.infra.persistence.UserEntity;

public class UserEntityMapper {
    public UserEntity toEntity(User userDomain) {
        return new UserEntity(
                userDomain.getId(),
                userDomain.getCpf(),
                userDomain.getName(),
                userDomain.getEmail(),
                userDomain.getPassword(),
                userDomain.getRole()
        );
    }

    public User toDomain(UserEntity entity) {

        UserRole role = (entity.getRole() != null) ? entity.getRole() : UserRole.USER;
        User user = new User(
                entity.getCpf(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                role
        );

        user.setId(entity.getId());
        return user;
    }
}
