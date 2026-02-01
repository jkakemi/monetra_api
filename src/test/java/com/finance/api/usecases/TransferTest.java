package com.finance.api.usecases;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.CategoryGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.application.usecases.transaction.Transfer;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;
import com.finance.api.domain.category.Category;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.transaction.TransactionType;
import com.finance.api.domain.user.User;
import com.finance.api.domain.user.UserRole;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferTest {

    @Mock
    private AccountGateway accountGateway;

    @Mock
    private TransactionGateway transactionGateway;

    @Mock
    private UserGateway userGateway;

    @Mock
    private CategoryGateway categoryGateway;

    private Transfer transfer;

    private User sender;
    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        transfer = new Transfer(accountGateway, transactionGateway, userGateway, categoryGateway);

        sender = new User("123.456.789-00", "João", "joao@email.com", "senha123", UserRole.USER);
        sender.setId(1L);

        sourceAccount = new Account(1L, "Conta Origem", AccountType.BANCO, "12345-6",
                new BigDecimal("1000.00"), new BigDecimal("500.00"));
        sourceAccount.setId(1L);

        targetAccount = new Account(2L, "Conta Destino", AccountType.BANCO, "65432-1",
                new BigDecimal("500.00"), BigDecimal.ZERO);
        targetAccount.setId(2L);
    }

    @Test
    @DisplayName("Should transfer successfully between accounts")
    void shouldTransferSuccessfully() {
        BigDecimal amount = new BigDecimal("100.00");

        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);
        when(accountGateway.findByAccountNumber("65432-1")).thenReturn(Optional.of(targetAccount));
        when(accountGateway.findByAccountNumberAndAccountType("65432-1", AccountType.BANCO))
                .thenReturn(Optional.of(targetAccount));
        when(transactionGateway.create(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "BANCO", amount, "Test transfer", null, null
        ));

        verify(accountGateway, times(2)).update(any(Account.class));
        verify(transactionGateway, times(2)).create(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when amount is zero or negative")
    void shouldThrowExceptionWhenAmountIsZeroOrNegative() {
        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);

        assertThrows(IllegalArgumentException.class, () -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "BANCO", BigDecimal.ZERO, "Test", null, null
        ));

        assertThrows(IllegalArgumentException.class, () -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "BANCO", new BigDecimal("-100"), "Test", null, null
        ));
    }

    @Test
    @DisplayName("Should throw exception when source account does not belong to user")
    void shouldThrowExceptionWhenSourceAccountNotBelongsToUser() {
        sourceAccount = new Account(99L, "Conta Outra", AccountType.BANCO, "99999-9",
                new BigDecimal("1000.00"), BigDecimal.ZERO);
        sourceAccount.setId(1L);

        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);

        assertThrows(IllegalArgumentException.class, () -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "BANCO", new BigDecimal("100"), "Test", null, null
        ));
    }

    @Test
    @DisplayName("Should throw exception when insufficient balance")
    void shouldThrowExceptionWhenInsufficientBalance() {
        sourceAccount = new Account(1L, "Conta Origem", AccountType.BANCO, "12345-6",
                new BigDecimal("50.00"), BigDecimal.ZERO);
        sourceAccount.setId(1L);

        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);

        assertThrows(IllegalArgumentException.class, () -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "BANCO", new BigDecimal("100"), "Test", null, null
        ));
    }

    @Test
    @DisplayName("Should throw exception when target account not found")
    void shouldThrowExceptionWhenTargetAccountNotFound() {
        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);
        when(accountGateway.findByAccountNumber("99999-9")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transfer.execute(
                "joao@email.com", 1L, "99999-9", "BANCO", new BigDecimal("100"), "Test", null, null
        ));
    }

    @Test
    @DisplayName("Should throw exception when source and target are the same")
    void shouldThrowExceptionWhenSameAccount() {
        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);
        when(accountGateway.findByAccountNumber("12345-6")).thenReturn(Optional.of(sourceAccount));

        assertThrows(IllegalArgumentException.class, () -> transfer.execute(
                "joao@email.com", 1L, "12345-6", "BANCO", new BigDecimal("100"), "Test", null, null
        ));
    }

    @Test
    @DisplayName("Should throw exception when target account type is blank")
    void shouldThrowExceptionWhenTargetTypeBlank() {
        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);
        when(accountGateway.findByAccountNumber("65432-1")).thenReturn(Optional.of(targetAccount));

        assertThrows(IllegalArgumentException.class, () -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "", new BigDecimal("100"), "Test", null, null
        ));
    }

    @Test
    @DisplayName("Should throw exception for invalid account type")
    void shouldThrowExceptionForInvalidAccountType() {
        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);
        when(accountGateway.findByAccountNumber("65432-1")).thenReturn(Optional.of(targetAccount));

        assertThrows(IllegalArgumentException.class, () -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "INVALID", new BigDecimal("100"), "Test", null, null
        ));
    }

    @Test
    @DisplayName("Should throw exception when category does not belong to user")
    void shouldThrowExceptionWhenCategoryNotBelongsToUser() {
        Category category = new Category(1L, "Transferencia", TransactionType.TRANSFERENCIA, 99L);
        category.setId(1L);

        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);
        when(accountGateway.findByAccountNumber("65432-1")).thenReturn(Optional.of(targetAccount));
        when(categoryGateway.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(IllegalArgumentException.class, () -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "BANCO", new BigDecimal("100"), "Test", null, 1L
        ));
    }

    @Test
    @DisplayName("Should transfer with custom date")
    void shouldTransferWithCustomDate() {
        LocalDate customDate = LocalDate.of(2025, 1, 15);

        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);
        when(accountGateway.findByAccountNumber("65432-1")).thenReturn(Optional.of(targetAccount));
        when(accountGateway.findByAccountNumberAndAccountType("65432-1", AccountType.BANCO))
                .thenReturn(Optional.of(targetAccount));
        when(transactionGateway.create(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "BANCO", new BigDecimal("100"), "Test", customDate, null
        ));

        verify(transactionGateway, times(2)).create(any(Transaction.class));
    }

    @Test
    @DisplayName("Should transfer with valid category")
    void shouldTransferWithValidCategory() {
        Category category = new Category(1L, "Transferencia", TransactionType.TRANSFERENCIA, 1L);
        category.setId(1L);

        when(userGateway.findByEmail("joao@email.com")).thenReturn(sender);
        when(accountGateway.findById(1L)).thenReturn(sourceAccount);
        when(accountGateway.findByAccountNumber("65432-1")).thenReturn(Optional.of(targetAccount));
        when(accountGateway.findByAccountNumberAndAccountType("65432-1", AccountType.BANCO))
                .thenReturn(Optional.of(targetAccount));
        when(categoryGateway.findById(1L)).thenReturn(Optional.of(category));
        when(transactionGateway.create(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> transfer.execute(
                "joao@email.com", 1L, "65432-1", "BANCO", new BigDecimal("100"), "Test", null, 1L
        ));
    }
}
