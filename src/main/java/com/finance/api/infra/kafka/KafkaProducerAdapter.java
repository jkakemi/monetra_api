package com.finance.api.infra.kafka;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.TransactionEventGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerAdapter implements TransactionEventGateway {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerAdapter.class);
    private static final String TOPIC = "transaction.requested";

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private final AccountGateway accountGateway;

    public KafkaProducerAdapter(KafkaTemplate<String, TransactionEvent> kafkaTemplate, AccountGateway accountGateway) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountGateway = accountGateway;
    }

    @Override
    public void publishTransactionCreated(Transaction transaction) {
        Account account = accountGateway.findById(transaction.getAccountId());

        TransactionEvent event = new TransactionEvent(
            transaction.getId(),
            transaction.getAccountId(),
            account.getUserId(),
            transaction.getFinalAmount(),
            transaction.getCurrency(),
            transaction.getType().name()
        );

        kafkaTemplate.send(TOPIC, String.valueOf(transaction.getId()), event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Transaction event sent: transactionId={}, topic={}",
                        transaction.getId(), TOPIC);
                } else {
                    logger.error("Failed to send transaction event: transactionId={}",
                        transaction.getId(), ex);
                }
            });
    }
}
