package com.finance.api.application.gateways;

import com.finance.api.domain.transaction.Transaction;

public interface TransactionEventGateway {
    void publishTransactionCreated(Transaction transaction);
}
