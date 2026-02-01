package com.finance.api.usecases;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.application.usecases.account.DeleteAccount;
import com.finance.api.application.usecases.account.UpdateAccount;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;
import com.finance.api.domain.user.User;
import com.finance.api.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountUseCasesTest {

    @Mock
    private AccountGateway accountGateway;

    @Mock
    private TransactionGateway transactionGateway;

    @Mock
    private UserGateway userGateway;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = new User("123.456.789-00", "João", "joao@email.com", "senha123", UserRole.USER);
        user.setId(1L);

        account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                new BigDecimal("1000.00"), new BigDecimal("500.00"));
        account.setId(1L);
    }

    @Nested
    @DisplayName("UpdateAccount Tests")
    class UpdateAccountTests {

        private UpdateAccount updateAccount;

        @BeforeEach
        void setUp() {
            updateAccount = new UpdateAccount(accountGateway, userGateway);
        }

        @Test
        @DisplayName("Should update account successfully")
        void shouldUpdateAccountSuccessfully() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(accountGateway.findById(1L)).thenReturn(account);

            assertDoesNotThrow(() -> updateAccount.execute(
                    "joao@email.com", 1L, "Nova Conta", new BigDecimal("1000.00")
            ));

            verify(accountGateway, times(1)).update(any(Account.class));
        }

        @Test
        @DisplayName("Should throw exception when account does not belong to user")
        void shouldThrowExceptionWhenAccountNotBelongsToUser() {
            Account otherAccount = new Account(99L, "Outra Conta", AccountType.BANCO, "99999-9",
                    BigDecimal.ZERO, BigDecimal.ZERO);
            otherAccount.setId(1L);

            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(accountGateway.findById(1L)).thenReturn(otherAccount);

            assertThrows(IllegalArgumentException.class, () -> updateAccount.execute(
                    "joao@email.com", 1L, "Nova Conta", new BigDecimal("1000.00")
            ));

            verify(accountGateway, never()).update(any());
        }
    }

    @Nested
    @DisplayName("DeleteAccount Tests")
    class DeleteAccountTests {

        private DeleteAccount deleteAccount;

        @BeforeEach
        void setUp() {
            deleteAccount = new DeleteAccount(accountGateway, transactionGateway, userGateway);
        }

        @Test
        @DisplayName("Should delete account successfully")
        void shouldDeleteAccountSuccessfully() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(accountGateway.findById(1L)).thenReturn(account);
            when(transactionGateway.existsByAccountId(1L)).thenReturn(false);

            assertDoesNotThrow(() -> deleteAccount.execute("joao@email.com", 1L));

            verify(accountGateway, times(1)).delete(1L);
        }

        @Test
        @DisplayName("Should throw exception when account does not belong to user")
        void shouldThrowExceptionWhenAccountNotBelongsToUser() {
            Account otherAccount = new Account(99L, "Outra Conta", AccountType.BANCO, "99999-9",
                    BigDecimal.ZERO, BigDecimal.ZERO);
            otherAccount.setId(1L);

            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(accountGateway.findById(1L)).thenReturn(otherAccount);

            assertThrows(IllegalArgumentException.class, () -> deleteAccount.execute("joao@email.com", 1L));

            verify(accountGateway, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw exception when account has transactions")
        void shouldThrowExceptionWhenAccountHasTransactions() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(accountGateway.findById(1L)).thenReturn(account);
            when(transactionGateway.existsByAccountId(1L)).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> deleteAccount.execute("joao@email.com", 1L));

            verify(accountGateway, never()).delete(any());
        }
    }
}
