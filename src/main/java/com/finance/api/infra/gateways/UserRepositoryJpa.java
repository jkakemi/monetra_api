package com.finance.api.infra.gateways;

import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.user.User;
import com.finance.api.infra.persistence.UserEntity;
import com.finance.api.infra.persistence.SpringUserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;


public class UserRepositoryJpa implements UserGateway {
    private final SpringUserRepository springUserRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryJpa(SpringUserRepository springUserRepository, UserEntityMapper mapper) {
        this.springUserRepository = springUserRepository;
        this.mapper = mapper;
    }

    @Override
    public User createUser(User user) {
        UserEntity entity = mapper.toEntity(user);
        springUserRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public List<User> listAll() {
        return springUserRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User findByEmail(String email) {
        UserEntity entity = springUserRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapper.toDomain(entity);
    }

    @Override
    public User updateUser(User user) {
        UserEntity entity = springUserRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        entity.setName(user.getName());
        entity.setPassword(user.getPassword());

        springUserRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public void deleteUser(String email) {
        UserEntity entity = springUserRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        springUserRepository.delete(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCpf(String number) {
        return springUserRepository.existsByCpf(number);
    }
}
