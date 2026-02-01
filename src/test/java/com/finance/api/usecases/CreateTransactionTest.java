package com.finance.api.usecases;

import com.finance.api.application.gateways.*;
import com.finance.api.application.usecases.transaction.CreateTransaction;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;
import com.finance.api.domain.category.Category;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.transaction.TransactionStatus;
import com.finance.api.domain.transaction.TransactionType;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionTest {

    @Mock
    private TransactionGateway transactionGateway;

    @Mock
    private AccountGateway accountGateway;

    @Mock
    private QuoteGateway quoteGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private TransactionEventGateway transactionEventGateway;

    private CreateTransaction createTransaction;

    @BeforeEach
    void setUp() {
        createTransaction = new CreateTransaction(
                transactionGateway,
                accountGateway,
                quoteGateway,
                categoryGateway,
                transactionEventGateway
        );
    }

    @Test
    @DisplayName("Should create RECEITA transaction with APPROVED status")
    void shouldCreateReceitaTransactionWithApprovedStatus() {
        Long accountId = 1L;
        Long categoryId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6", BigDecimal.ZERO, BigDecimal.ZERO);
        account.setId(accountId);

        Category category = new Category(1L, "Salário", TransactionType.RECEITA, 1L);
        category.setId(categoryId);

        when(accountGateway.findById(accountId)).thenReturn(account);
        when(categoryGateway.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionGateway.create(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        Transaction result = createTransaction.execute(
                accountId, categoryId, amount, "BRL", "RECEITA", "Salário mensal", LocalDate.now()
        );

        assertNotNull(result);
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertEquals(amount, result.getFinalAmount());

        verify(accountGateway, times(1)).update(any(Account.class));
        verify(transactionGateway, times(1)).create(any(Transaction.class));
        verify(transactionEventGateway, never()).publishTransactionCreated(any());
    }

    @Test
    @DisplayName("Should create DESPESA transaction with PENDING status and publish event")
    void shouldCreateDespesaTransactionWithPendingStatus() {
        Long accountId = 1L;
        Long categoryId = 1L;
        BigDecimal amount = new BigDecimal("50.00");

        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                new BigDecimal("1000.00"), BigDecimal.ZERO);
        account.setId(accountId);

        Category category = new Category(1L, "Alimentação", TransactionType.DESPESA, 1L);
        category.setId(categoryId);

        when(accountGateway.findById(accountId)).thenReturn(account);
        when(categoryGateway.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionGateway.create(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        Transaction result = createTransaction.execute(
                accountId, categoryId, amount, "BRL", "DESPESA", "Almoço", LocalDate.now()
        );

        assertNotNull(result);
        assertEquals(TransactionStatus.PENDING, result.getStatus());

        verify(accountGateway, never()).update(any(Account.class));
        verify(transactionGateway, times(1)).create(any(Transaction.class));
        verify(transactionEventGateway, times(1)).publishTransactionCreated(any(Transaction.class));
    }

    @Test
    @DisplayName("Should apply currency quotation for non-BRL transactions")
    void shouldApplyCurrencyQuotation() {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal rate = new BigDecimal("5.50");

        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6",
                new BigDecimal("1000.00"), BigDecimal.ZERO);
        account.setId(accountId);

        when(accountGateway.findById(accountId)).thenReturn(account);
        when(quoteGateway.getQuote(eq("USD"), any(LocalDate.class))).thenReturn(rate);
        when(transactionGateway.create(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        Transaction result = createTransaction.execute(
                accountId, null, amount, "USD", "DESPESA", "Compra internacional", LocalDate.now()
        );

        assertNotNull(result);
        assertEquals(0, new BigDecimal("550.00").compareTo(result.getFinalAmount()));
        assertEquals(0, rate.compareTo(result.getQuotation()));

        verify(quoteGateway, times(1)).getQuote(eq("USD"), any(LocalDate.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid category type")
    void shouldThrowExceptionForInvalidCategoryType() {
        Long accountId = 1L;
        Long categoryId = 1L;

        Account account = new Account(1L, "Conta Corrente", AccountType.BANCO, "12345-6", BigDecimal.ZERO, BigDecimal.ZERO);
        account.setId(accountId);

        Category category = new Category(1L, "Salário", TransactionType.RECEITA, 1L);
        category.setId(categoryId);

        when(accountGateway.findById(accountId)).thenReturn(account);
        when(categoryGateway.findById(categoryId)).thenReturn(Optional.of(category));

        assertThrows(IllegalArgumentException.class, () -> {
            createTransaction.execute(
                    accountId, categoryId, BigDecimal.TEN, "BRL", "DESPESA", "Teste", LocalDate.now()
            );
        });

        verify(transactionGateway, never()).create(any());
    }
}
