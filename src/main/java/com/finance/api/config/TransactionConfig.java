package com.finance.api.config;

import com.finance.api.application.gateways.*;
import com.finance.api.application.usecases.transaction.CreateTransaction;
import com.finance.api.application.usecases.transaction.DeleteTransaction;
import com.finance.api.application.usecases.transaction.ListTransactions;
import com.finance.api.application.usecases.transaction.Transfer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {
    @Bean
    CreateTransaction createTransaction(
            TransactionGateway transactionGateway,
            AccountGateway accountGateway,
            QuoteGateway quoteGateway,
            CategoryGateway categoryGateway,
            TransactionEventGateway transactionEventGateway) {
        return new CreateTransaction(transactionGateway, accountGateway, quoteGateway, categoryGateway, transactionEventGateway);
    }
    @Bean
    ListTransactions listTransactions(TransactionGateway transactionGateway, AccountGateway accountGateway, UserGateway userGateway) {
        return new ListTransactions(transactionGateway, accountGateway, userGateway);
    }

    @Bean
    DeleteTransaction deleteTransaction(TransactionGateway transactionGateway, AccountGateway accountGateway, UserGateway userGateway) {
        return new DeleteTransaction(transactionGateway, accountGateway, userGateway);
    }

    @Bean
    Transfer transfer(AccountGateway accountGateway, TransactionGateway transactionGateway, UserGateway userGateway, CategoryGateway categoryGateway) {
        return new Transfer(accountGateway, transactionGateway, userGateway, categoryGateway);
    }
}
