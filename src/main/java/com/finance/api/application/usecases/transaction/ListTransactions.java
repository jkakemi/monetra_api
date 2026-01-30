package com.finance.api.application.usecases.transaction;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.user.User;

import java.util.List;

public class ListTransactions {
    private final TransactionGateway transactionGateway;
    private final AccountGateway accountGateway;
    private final UserGateway userGateway;

    public ListTransactions(TransactionGateway transactionGateway, AccountGateway accountGateway, UserGateway userGateway) {
        this.transactionGateway = transactionGateway;
        this.accountGateway = accountGateway;
        this.userGateway = userGateway;
    }

    public List<Transaction> execute(String userEmail, Long accountId) {
        User user = userGateway.findByEmail(userEmail);
        Account account = accountGateway.findById(accountId);

        if (!account.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("This account does not belong to this account");
        }

        return transactionGateway.listByAccountId(accountId);
    }
}