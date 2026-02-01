package com.finance.api.kafka;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.BankIntegrationGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.transaction.TransactionStatus;
import com.finance.api.domain.transaction.TransactionType;
import com.finance.api.infra.kafka.TransactionEvent;
import com.finance.api.infra.kafka.TransactionProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionProcessorTest {

    @Mock
    private TransactionGateway transactionGateway;

    @Mock
    private AccountGateway accountGateway;

    @Mock
    private BankIntegrationGateway bankIntegrationGateway;

    @Mock
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    private TransactionProcessor transactionProcessor;

    @BeforeEach
    void setUp() {
        transactionProcessor = new TransactionProcessor(
                transactionGateway,
                accountGateway,
                bankIntegrationGateway,
                kafkaTemplate
        );
    }

    @Test
    @DisplayName("Should approve transaction and update balance when sufficient balance")
    void shouldApproveTransactionWhenSufficientBalance() {
        Long transactionId = 1L;
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("50.00");

        Transaction transaction = new Transaction(accountId, null, amount, "BRL",
                TransactionType.DESPESA, "Test", LocalDate.now());
        transaction.setId(transactionId);

        Account account = new Account(1L, "Conta", AccountType.FISICO, null,
                new BigDecimal("100.00"), BigDecimal.ZERO);
        account.setId(accountId);

        TransactionEvent event = new TransactionEvent(transactionId, accountId, 1L, amount, "BRL", "DESPESA");

        when(transactionGateway.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(accountGateway.findById(accountId)).thenReturn(account);

        transactionProcessor.processTransaction(event);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionGateway).update(transactionCaptor.capture());
        assertEquals(TransactionStatus.APPROVED, transactionCaptor.getValue().getStatus());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountGateway).update(accountCaptor.capture());
        assertEquals(0, new BigDecimal("50.00").compareTo(accountCaptor.getValue().getBalance()));
    }

    @Test
    @DisplayName("Should reject transaction and NOT update balance when insufficient balance")
    void shouldRejectTransactionWhenInsufficientBalance() {
        Long transactionId = 1L;
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("200.00");

        Transaction transaction = new Transaction(accountId, null, amount, "BRL",
                TransactionType.DESPESA, "Test", LocalDate.now());
        transaction.setId(transactionId);

        Account account = new Account(1L, "Conta", AccountType.FISICO, null,
                new BigDecimal("50.00"), BigDecimal.ZERO);
        account.setId(accountId);

        TransactionEvent event = new TransactionEvent(transactionId, accountId, 1L, amount, "BRL", "DESPESA");

        when(transactionGateway.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(accountGateway.findById(accountId)).thenReturn(account);

        transactionProcessor.processTransaction(event);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionGateway).update(captor.capture());
        assertEquals(TransactionStatus.REJECTED, captor.getValue().getStatus());

        verify(accountGateway, never()).update(any());
    }

    @Test
    @DisplayName("Should always approve RECEITA transactions and ADD to balance")
    void shouldAlwaysApproveReceitaTransactions() {
        Long transactionId = 1L;
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("1000.00");

        Transaction transaction = new Transaction(accountId, null, amount, "BRL",
                TransactionType.RECEITA, "Salário", LocalDate.now());
        transaction.setId(transactionId);

        Account account = new Account(1L, "Conta", AccountType.FISICO, null,
                BigDecimal.ZERO, BigDecimal.ZERO);
        account.setId(accountId);

        TransactionEvent event = new TransactionEvent(transactionId, accountId, 1L, amount, "BRL", "RECEITA");

        when(transactionGateway.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(accountGateway.findById(accountId)).thenReturn(account);

        transactionProcessor.processTransaction(event);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionGateway).update(transactionCaptor.capture());
        assertEquals(TransactionStatus.APPROVED, transactionCaptor.getValue().getStatus());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountGateway).update(accountCaptor.capture());
        assertEquals(0, new BigDecimal("1000.00").compareTo(accountCaptor.getValue().getBalance()));
    }

    @Test
    @DisplayName("Should consider credit limit when checking balance and update balance")
    void shouldConsiderCreditLimitWhenCheckingBalance() {
        Long transactionId = 1L;
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("150.00");

        Transaction transaction = new Transaction(accountId, null, amount, "BRL",
                TransactionType.DESPESA, "Compra", LocalDate.now());
        transaction.setId(transactionId);

        Account account = new Account(1L, "Conta", AccountType.CARTAO_CREDITO, null,
                new BigDecimal("50.00"), new BigDecimal("200.00"));
        account.setId(accountId);

        TransactionEvent event = new TransactionEvent(transactionId, accountId, 1L, amount, "BRL", "DESPESA");

        when(transactionGateway.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(accountGateway.findById(accountId)).thenReturn(account);

        transactionProcessor.processTransaction(event);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionGateway).update(transactionCaptor.capture());
        assertEquals(TransactionStatus.APPROVED, transactionCaptor.getValue().getStatus());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountGateway).update(accountCaptor.capture());
        assertEquals(0, new BigDecimal("-100.00").compareTo(accountCaptor.getValue().getBalance()));
    }

    @Test
    @DisplayName("Should consider external bank balance for BANCO accounts and update balance")
    void shouldConsiderExternalBankBalance() {
        Long transactionId = 1L;
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("150.00");

        Transaction transaction = new Transaction(accountId, null, amount, "BRL",
                TransactionType.DESPESA, "Compra", LocalDate.now());
        transaction.setId(transactionId);

        Account account = new Account(1L, "Conta", AccountType.BANCO, "12345-6",
                new BigDecimal("50.00"), BigDecimal.ZERO);
        account.setId(accountId);

        TransactionEvent event = new TransactionEvent(transactionId, accountId, 1L, amount, "BRL", "DESPESA");

        when(transactionGateway.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(accountGateway.findById(accountId)).thenReturn(account);
        when(bankIntegrationGateway.getBalanceFromExternalBank("12345-6"))
                .thenReturn(new BigDecimal("200.00"));

        transactionProcessor.processTransaction(event);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionGateway).update(transactionCaptor.capture());
        assertEquals(TransactionStatus.APPROVED, transactionCaptor.getValue().getStatus());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountGateway).update(accountCaptor.capture());
        assertEquals(0, new BigDecimal("-100.00").compareTo(accountCaptor.getValue().getBalance()));

        verify(bankIntegrationGateway).getBalanceFromExternalBank("12345-6");
    }

    @Test
    @DisplayName("Should send to DLQ when transaction not found")
    void shouldSendToDlqWhenTransactionNotFound() {
        Long transactionId = 999L;
        TransactionEvent event = new TransactionEvent(transactionId, 1L, 1L,
                BigDecimal.TEN, "BRL", "DESPESA");

        when(transactionGateway.findById(transactionId)).thenReturn(Optional.empty());

        transactionProcessor.processTransaction(event);

        verify(kafkaTemplate).send(eq("transaction.dlq"), anyString(), eq(event));
        verify(transactionGateway, never()).update(any());
    }
}
