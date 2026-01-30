package com.finance.api.application.usecases.transaction;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.transaction.TransactionType;
import com.finance.api.domain.user.User;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;

public class DeleteTransaction {
    private final TransactionGateway transactionGateway;
    private final AccountGateway accountGateway;
    private final UserGateway userGateway;

    public DeleteTransaction(TransactionGateway transactionGateway, AccountGateway accountGateway, UserGateway userGateway) {
        this.transactionGateway = transactionGateway;
        this.accountGateway = accountGateway;
        this.userGateway = userGateway;
    }

    public void execute(String userEmail, Long transactionId) {
        Transaction transaction = transactionGateway.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        Account account = accountGateway.findById(transaction.getAccountId());
        User user = userGateway.findByEmail(userEmail);

        if (!account.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("This transaction does not belong to this account");
        }

        BigDecimal reverseValue = transaction.getFinalAmount();

        if (transaction.getType() == TransactionType.RECEITA) {
            BigDecimal newBalance = account.getBalance().subtract(reverseValue);
            account.setBalance(newBalance);
        }
        else {
            BigDecimal newBalance = account.getBalance().add(reverseValue);
            account.setBalance(newBalance);
        }

        accountGateway.update(account);
        transactionGateway.delete(transactionId);
    }
}