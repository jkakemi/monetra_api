package com.finance.api.config;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.BankIntegrationGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.application.usecases.account.CreateAccount;
import com.finance.api.application.usecases.account.DeleteAccount;
import com.finance.api.application.usecases.account.ListMyAccounts;
import com.finance.api.application.usecases.account.UpdateAccount;
import com.finance.api.infra.gateways.AccountEntityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

    @Bean
    public CreateAccount createAccount(AccountGateway accountGateway,
                                       BankIntegrationGateway bankIntegrationGateway,
                                       UserGateway userGateway) {
        return new CreateAccount(accountGateway, bankIntegrationGateway, userGateway);
    }

    @Bean
    public ListMyAccounts listMyAccounts(AccountGateway accountGateway,
                                         UserGateway userGateway) {
        return new ListMyAccounts(accountGateway, userGateway);
    }

    @Bean
    UpdateAccount updateAccount(AccountGateway accountGateway, UserGateway userGateway) { return new UpdateAccount(accountGateway, userGateway); }

    @Bean
    DeleteAccount deleteAccount(AccountGateway accountGateway, TransactionGateway transactionGateway, UserGateway userGateway) {
        return new DeleteAccount(accountGateway, transactionGateway, userGateway);
    }


    @Bean
    AccountEntityMapper accountEntityMapper() {
        return new AccountEntityMapper();
    }
}
