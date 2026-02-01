package com.finance.api.infra.controller;

import com.finance.api.application.usecases.user.CreateUser;
import com.finance.api.application.usecases.user.LoginUser;
import com.finance.api.domain.user.User;
import com.finance.api.infra.controller.dto.LoginRequestDTO;
import com.finance.api.infra.controller.dto.TokenResponseDTO;
import com.finance.api.infra.controller.dto.UserRequestDTO;
import com.finance.api.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUser loginUser;
    private final CreateUser createUser;
    private final TokenService tokenService;

    public AuthController(LoginUser loginUser, CreateUser createUser, TokenService tokenService) {
        this.loginUser = loginUser;
        this.createUser = createUser;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        User authenticatedUser = loginUser.execute(data.email(), data.password());

        String token = tokenService.generateToken(authenticatedUser);

        return ResponseEntity.ok(new TokenResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRequestDTO data) {
        createUser.execute(data.name(), data.email(), data.password(), data.cpf());
        return ResponseEntity.ok().build();
    }
}