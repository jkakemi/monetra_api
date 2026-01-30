package com.finance.api.application.usecases.account;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.user.User;

public class DeleteAccount {
    private final AccountGateway accountGateway;
    private final TransactionGateway transactionGateway;
    private final UserGateway userGateway;

    public DeleteAccount(AccountGateway accountGateway, TransactionGateway transactionGateway, UserGateway userGateway) {
        this.accountGateway = accountGateway;
        this.transactionGateway = transactionGateway;
        this.userGateway = userGateway;
    }

    public void execute(String userEmail, Long accountId) {
        User user = userGateway.findByEmail(userEmail);
        Account account = accountGateway.findById(accountId);

        if (!account.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("This account does not belong to this account");
        }

        if (transactionGateway.existsByAccountId(accountId)) {
            throw new IllegalStateException("It is not possible to delete this account. You have transactions yet");
        }

        accountGateway.delete(accountId);
    }
}