package com.finance.api.domain;

import com.finance.api.domain.user.Cpf;
import com.finance.api.domain.user.Email;
import com.finance.api.domain.user.User;
import com.finance.api.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {

        @Test
        @DisplayName("Should create user with all fields")
        void shouldCreateUserWithAllFields() {
            User user = new User("123.456.789-00", "João Silva", "joao@email.com", "senha123", UserRole.USER);

            assertEquals("João Silva", user.getName());
            assertEquals("joao@email.com", user.getEmail());
            assertEquals("123.456.789-00", user.getCpf());
            assertEquals("senha123", user.getPassword());
            assertEquals(UserRole.USER, user.getRole());
        }

        @Test
        @DisplayName("Should create user with null role defaulting to USER")
        void shouldCreateUserWithNullRole() {
            User user = new User("123.456.789-00", "João Silva", "joao@email.com", "senha123", null);

            assertEquals(UserRole.USER, user.getRole());
        }

        @Test
        @DisplayName("Should create user with ADMIN role")
        void shouldCreateUserWithAdminRole() {
            User user = new User("123.456.789-00", "Admin", "admin@email.com", "senha123", UserRole.ADMIN);

            assertEquals(UserRole.ADMIN, user.getRole());
        }

        @Test
        @DisplayName("Should set and get id")
        void shouldSetAndGetId() {
            User user = new User("123.456.789-00", "João Silva", "joao@email.com", "senha123", UserRole.USER);

            user.setId(10L);
            assertEquals(10L, user.getId());
        }
    }

    @Nested
    @DisplayName("User Update Tests")
    class UserUpdateTests {

        @Test
        @DisplayName("Should update name successfully")
        void shouldUpdateNameSuccessfully() {
            User user = new User("123.456.789-00", "João Silva", "joao@email.com", "senha123", UserRole.USER);

            user.setName("João Santos");
            assertEquals("João Santos", user.getName());
        }

        @Test
        @DisplayName("Should throw exception when setting null name")
        void shouldThrowExceptionWhenNameIsNull() {
            User user = new User("123.456.789-00", "João Silva", "joao@email.com", "senha123", UserRole.USER);

            assertThrows(IllegalArgumentException.class, () -> user.setName(null));
        }

        @Test
        @DisplayName("Should throw exception when setting empty name")
        void shouldThrowExceptionWhenNameIsEmpty() {
            User user = new User("123.456.789-00", "João Silva", "joao@email.com", "senha123", UserRole.USER);

            assertThrows(IllegalArgumentException.class, () -> user.setName("   "));
        }

        @Test
        @DisplayName("Should update password successfully")
        void shouldUpdatePasswordSuccessfully() {
            User user = new User("123.456.789-00", "João Silva", "joao@email.com", "senha123", UserRole.USER);

            user.setPassword("novaSenha123");
            assertEquals("novaSenha123", user.getPassword());
        }

        @Test
        @DisplayName("Should throw exception when password is too short")
        void shouldThrowExceptionWhenPasswordIsTooShort() {
            User user = new User("123.456.789-00", "João Silva", "joao@email.com", "senha123", UserRole.USER);

            assertThrows(IllegalArgumentException.class, () -> user.setPassword("12345"));
        }

        @Test
        @DisplayName("Should throw exception when password is null")
        void shouldThrowExceptionWhenPasswordIsNull() {
            User user = new User("123.456.789-00", "João Silva", "joao@email.com", "senha123", UserRole.USER);

            assertThrows(IllegalArgumentException.class, () -> user.setPassword(null));
        }
    }

    @Nested
    @DisplayName("CPF Tests")
    class CpfTests {

        @Test
        @DisplayName("Should create valid CPF")
        void shouldCreateValidCpf() {
            Cpf cpf = new Cpf("123.456.789-00");
            assertEquals("123.456.789-00", cpf.getNumber());
        }

        @Test
        @DisplayName("Should throw exception for null CPF")
        void shouldThrowExceptionForNullCpf() {
            assertThrows(IllegalArgumentException.class, () -> new Cpf(null));
        }

        @Test
        @DisplayName("Should throw exception for invalid CPF format")
        void shouldThrowExceptionForInvalidCpfFormat() {
            assertThrows(IllegalArgumentException.class, () -> new Cpf("12345678900"));
            assertThrows(IllegalArgumentException.class, () -> new Cpf("123-456-789-00"));
            assertThrows(IllegalArgumentException.class, () -> new Cpf("abc.def.ghi-jk"));
        }
    }

    @Nested
    @DisplayName("Email Tests")
    class EmailTests {

        @Test
        @DisplayName("Should create valid email")
        void shouldCreateValidEmail() {
            Email email = new Email("joao@email.com");
            assertEquals("joao@email.com", email.getAddress());
        }

        @Test
        @DisplayName("Should throw exception for null email")
        void shouldThrowExceptionForNullEmail() {
            assertThrows(IllegalArgumentException.class, () -> new Email(null));
        }

        @Test
        @DisplayName("Should throw exception for invalid email format")
        void shouldThrowExceptionForInvalidEmailFormat() {
            assertThrows(IllegalArgumentException.class, () -> new Email("joao"));
            assertThrows(IllegalArgumentException.class, () -> new Email("joao@"));
            assertThrows(IllegalArgumentException.class, () -> new Email("@email.com"));
            assertThrows(IllegalArgumentException.class, () -> new Email("joao@email"));
        }

        @Test
        @DisplayName("Should accept valid email formats")
        void shouldAcceptValidEmailFormats() {
            assertDoesNotThrow(() -> new Email("user@domain.com"));
            assertDoesNotThrow(() -> new Email("user.name@domain.com"));
            assertDoesNotThrow(() -> new Email("user-name@domain.com"));
            assertDoesNotThrow(() -> new Email("user@sub.domain.com"));
        }
    }
}
