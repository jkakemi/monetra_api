package com.finance.api.config;


import com.finance.api.application.gateways.PasswordEncoderGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.application.usecases.user.*;
import com.finance.api.infra.gateways.AccountEntityMapper;
import com.finance.api.infra.gateways.UserEntityMapper;
import com.finance.api.infra.gateways.UserRepositoryJpa;
import com.finance.api.infra.persistence.SpringUserRepository;
import com.finance.api.infra.security.BCryptPasswordEncoderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    PasswordEncoderGateway passwordEncoderGateway() {
        return new BCryptPasswordEncoderImpl();
    }

    @Bean
    LoginUser loginUser(UserGateway userGateway, PasswordEncoderGateway passwordEncoder) {
        return new LoginUser(userGateway, passwordEncoder);
    }

    @Bean
    CreateUser createUser(UserGateway userGateway, PasswordEncoderGateway passwordEncoder) {
        return new CreateUser(userGateway, passwordEncoder);
    }

    @Bean
    GetUserProfile getUserProfile(UserGateway userGateway) {
        return new GetUserProfile(userGateway);
    }

    @Bean
    UpdateUser updateUser(UserGateway userGateway, PasswordEncoderGateway passwordEncoder) {
        return new UpdateUser(userGateway, passwordEncoder);
    }

    @Bean
    DeleteUser deleteUser(UserGateway userGateway) {
        return new DeleteUser(userGateway);
    }

    @Bean
    UserGateway userGateway(SpringUserRepository springUserRepository, UserEntityMapper mapper) {
        return new UserRepositoryJpa(springUserRepository, mapper);
    }

    @Bean
    UserEntityMapper userEntityMapper() {
        return new UserEntityMapper();
    }

    @Bean
    ImportUsersFromExcel importUsersFromExcel(UserGateway userGateway, PasswordEncoderGateway passwordEncoder) {
        return new ImportUsersFromExcel(userGateway, passwordEncoder);
    }

}
