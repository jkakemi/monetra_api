package com.finance.api.application.usecases.account;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.user.User;

import java.util.List;

public class ListMyAccounts {
    private final AccountGateway accountGateway;
    private final UserGateway userGateway;

    public ListMyAccounts(AccountGateway accountGateway, UserGateway userGateway) {
        this.accountGateway = accountGateway;
        this.userGateway = userGateway;
    }

    public List<Account> execute(String email){
        User user = userGateway.findByEmail(email);
        return accountGateway.listByUserId(user.getId());
    }
}
