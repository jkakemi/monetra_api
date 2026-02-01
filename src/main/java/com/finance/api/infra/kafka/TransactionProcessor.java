package com.finance.api.infra.kafka;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.BankIntegrationGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.transaction.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);
    private static final String DLQ_TOPIC = "transaction.dlq";

    private final TransactionGateway transactionGateway;
    private final AccountGateway accountGateway;
    private final BankIntegrationGateway bankIntegrationGateway;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionProcessor(
            TransactionGateway transactionGateway,
            AccountGateway accountGateway,
            BankIntegrationGateway bankIntegrationGateway,
            KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.transactionGateway = transactionGateway;
        this.accountGateway = accountGateway;
        this.bankIntegrationGateway = bankIntegrationGateway;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "transaction.requested", groupId = "transaction-processor")
    public void processTransaction(TransactionEvent event) {
        logger.info("Processing transaction event: transactionId={}", event.transactionId());

        try {
            Transaction transaction = transactionGateway.findById(event.transactionId())
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + event.transactionId()));

            Account account = accountGateway.findById(event.accountId());

            boolean approved = evaluateTransaction(transaction, account);

            if (approved) {
                transaction.approve();

                BigDecimal transactionAmount = transaction.getFinalAmount();

                if (transaction.getType() == TransactionType.RECEITA) {
                    account.setBalance(account.getBalance().add(transactionAmount));
                } else {
                    account.setBalance(account.getBalance().subtract(transactionAmount));
                }

                accountGateway.update(account);

                logger.info("Transaction APPROVED: transactionId={}, type={}, amount={}, newBalance={}",
                        event.transactionId(), transaction.getType(), transactionAmount, account.getBalance());
            } else {
                transaction.reject();
                logger.info("Transaction REJECTED: transactionId={}, reason=insufficient balance",
                        event.transactionId());
            }

            transactionGateway.update(transaction);

        } catch (Exception e) {
            logger.error("Error processing transaction: transactionId={}", event.transactionId(), e);
            sendToDlq(event);
        }
    }

    private boolean evaluateTransaction(Transaction transaction, Account account) {
        if (transaction.getType() == TransactionType.RECEITA) {
            return true;
        }

        BigDecimal availableBalance = getAvailableBalance(account);
        BigDecimal requiredAmount = transaction.getFinalAmount();

        return availableBalance.compareTo(requiredAmount) >= 0;
    }

    private BigDecimal getAvailableBalance(Account account) {
        BigDecimal internalBalance = account.getBalance().add(account.getCreditLimit());

        if (account.getType() == AccountType.BANCO && account.getAccountNumber() != null) {
            try {
                BigDecimal externalBalance = bankIntegrationGateway.getBalanceFromExternalBank(account.getAccountNumber());
                if (externalBalance != null) {
                    return internalBalance.add(externalBalance);
                }
            } catch (Exception e) {
                logger.warn("Failed to get external balance for account {}: {}",
                        account.getAccountNumber(), e.getMessage());
            }
        }

        return internalBalance;
    }

    private void sendToDlq(TransactionEvent event) {
        try {
            kafkaTemplate.send(DLQ_TOPIC, String.valueOf(event.transactionId()), event);
            logger.info("Transaction sent to DLQ: transactionId={}", event.transactionId());
        } catch (Exception e) {
            logger.error("Failed to send transaction to DLQ: transactionId={}", event.transactionId(), e);
        }
    }
}
