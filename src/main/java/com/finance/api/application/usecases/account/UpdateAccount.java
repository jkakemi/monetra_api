package com.finance.api.application.usecases.account;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.user.User;
import java.math.BigDecimal;

public class UpdateAccount {
    private final AccountGateway accountGateway;
    private final UserGateway userGateway;

    public UpdateAccount(AccountGateway accountGateway, UserGateway userGateway) {
        this.accountGateway = accountGateway;
        this.userGateway = userGateway;
    }

    public void execute(String userEmail, Long accountId, String newName, BigDecimal newLimit) {
        User user = userGateway.findByEmail(userEmail);
        Account account = accountGateway.findById(accountId);

        if (!account.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("This account does not belong to this account");
        }

        account.update(newName, newLimit);

        accountGateway.update(account);
    }
}