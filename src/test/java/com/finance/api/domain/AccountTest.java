package com.finance.api.domain;

import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    @DisplayName("Should create account with all fields")
    void shouldCreateAccountWithAllFields() {
        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                new BigDecimal("1000.00"), new BigDecimal("500.00"));

        assertEquals("Conta Corrente", account.getName());
        assertEquals(AccountType.BANCO, account.getType());
        assertEquals("12345-6", account.getAccountNumber());
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
        assertEquals(new BigDecimal("500.00"), account.getCreditLimit());
        assertEquals(1L, account.getUserId());
    }

    @Test
    @DisplayName("Should create account with null balance and credit limit")
    void shouldCreateAccountWithNullBalanceAndCreditLimit() {
        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6", null, null);

        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals(BigDecimal.ZERO, account.getCreditLimit());
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Account(1L, null, AccountType.BANCO, "12345-6", BigDecimal.ZERO, BigDecimal.ZERO)
        );
    }

    @Test
    @DisplayName("Should throw exception when name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
            new Account(1L, "  ", AccountType.BANCO, "12345-6", BigDecimal.ZERO, BigDecimal.ZERO)
        );
    }

    @Test
    @DisplayName("Should throw exception when type is null")
    void shouldThrowExceptionWhenTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Account(1L, "Conta Corrente", null, "12345-6", BigDecimal.ZERO, BigDecimal.ZERO)
        );
    }

    @Test
    @DisplayName("Should update account name and credit limit")
    void shouldUpdateAccountNameAndCreditLimit() {
        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                new BigDecimal("1000.00"), new BigDecimal("500.00"));

        account.update("Nova Conta", new BigDecimal("1000.00"));

        assertEquals("Nova Conta", account.getName());
        assertEquals(new BigDecimal("1000.00"), account.getCreditLimit());
    }

    @Test
    @DisplayName("Should not update name when blank")
    void shouldNotUpdateNameWhenBlank() {
        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                new BigDecimal("1000.00"), new BigDecimal("500.00"));

        account.update("  ", new BigDecimal("1000.00"));

        assertEquals("Conta Corrente", account.getName());
    }

    @Test
    @DisplayName("Should not update credit limit when null")
    void shouldNotUpdateCreditLimitWhenNull() {
        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                new BigDecimal("1000.00"), new BigDecimal("500.00"));

        account.update("Nova Conta", null);

        assertEquals(new BigDecimal("500.00"), account.getCreditLimit());
    }

    @Test
    @DisplayName("Should set and get id")
    void shouldSetAndGetId() {
        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                BigDecimal.ZERO, BigDecimal.ZERO);

        account.setId(10L);
        assertEquals(10L, account.getId());
    }

    @Test
    @DisplayName("Should set balance")
    void shouldSetBalance() {
        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                BigDecimal.ZERO, BigDecimal.ZERO);

        account.setBalance(new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("2000.00"), account.getBalance());
    }

    @Test
    @DisplayName("Should set credit limit")
    void shouldSetCreditLimit() {
        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                BigDecimal.ZERO, BigDecimal.ZERO);

        account.setCreditLimit(new BigDecimal("1500.00"));
        assertEquals(new BigDecimal("1500.00"), account.getCreditLimit());
    }

    @Test
    @DisplayName("Should create account with FISICO type")
    void shouldCreateAccountWithFisicoType() {
        Account account = new Account(1L, "Carteira", AccountType.FISICO, "CASH-001",
                new BigDecimal("100.00"), BigDecimal.ZERO);

        assertEquals(AccountType.FISICO, account.getType());
    }
}
