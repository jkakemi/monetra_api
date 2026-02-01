package com.finance.api.usecases;

import com.finance.api.application.gateways.PasswordEncoderGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.application.usecases.user.CreateUser;
import com.finance.api.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordEncoderGateway passwordEncoder;

    private CreateUser createUser;

    @BeforeEach
    void setUp() {
        createUser = new CreateUser(userGateway, passwordEncoder);
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        String cpf = "123.456.789-00";
        String name = "João Silva";
        String email = "joao@email.com";
        String password = "senha123";
        String encodedPassword = "encoded_senha123";

        when(userGateway.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userGateway.createUser(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = createUser.execute(name, email, password, cpf);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(name, result.getName());
        assertEquals(encodedPassword, result.getPassword());

        verify(passwordEncoder, times(1)).encode(password);
        verify(userGateway, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception for existing email")
    void shouldThrowExceptionForExistingEmail() {
        String cpf = "123.456.789-00";
        String name = "João Silva";
        String email = "joao@email.com";
        String password = "senha123";

        when(userGateway.existsByEmail(email)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            createUser.execute(name, email, password, cpf);
        });

        verify(userGateway, never()).createUser(any());
    }

    @Test
    @DisplayName("Should throw exception for short password")
    void shouldThrowExceptionForShortPassword() {
        String cpf = "123.456.789-00";
        String name = "João Silva";
        String email = "joao@email.com";
        String shortPassword = "123";

        when(userGateway.existsByEmail(email)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            createUser.execute(name, email, shortPassword, cpf);
        });

        verify(userGateway, never()).createUser(any());
    }

    @Test
    @DisplayName("Should throw exception for blank CPF")
    void shouldThrowExceptionForBlankCpf() {
        String name = "João Silva";
        String email = "joao@email.com";
        String password = "senha123";

        when(userGateway.existsByEmail(email)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            createUser.execute(name, email, password, "");
        });

        verify(userGateway, never()).createUser(any());
    }
}
