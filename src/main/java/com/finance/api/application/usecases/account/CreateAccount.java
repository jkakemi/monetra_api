package com.finance.api.application.usecases.account;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.BankIntegrationGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;
import com.finance.api.domain.user.User;

import java.math.BigDecimal;

public class CreateAccount {

    private final AccountGateway accountGateway;
    private final BankIntegrationGateway bankIntegrationGateway;
    private final UserGateway userGateway;

    public CreateAccount(AccountGateway accountGateway, BankIntegrationGateway bankIntegrationGateway, UserGateway userGateway) {
        this.accountGateway = accountGateway;
        this.bankIntegrationGateway = bankIntegrationGateway;
        this.userGateway = userGateway;
    }

    public void execute(String userEmail, String name, String typeStr, String accountNumberDto, BigDecimal initialBalance, BigDecimal creditLimitDto) {
        User user = userGateway.findByEmail(userEmail);
        AccountType type;
        try {
            type = AccountType.valueOf(typeStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid type: BANCO, FISICO or CARTAO_CREDITO");
        }

        String finalAccountNumber = accountNumberDto;

        if (type == AccountType.FISICO) {
            if (finalAccountNumber == null || finalAccountNumber.isBlank()) {
                finalAccountNumber = "CARTEIRA";
            }
        }
        else {
            if (finalAccountNumber == null || finalAccountNumber.isBlank()) {
                throw new IllegalArgumentException("Account number is required");
            }
        }

        if (accountGateway.existsByAccountNumber(finalAccountNumber)) {
            Account existing = accountGateway.findByAccountNumber(finalAccountNumber).get();
            if (!existing.getUserId().equals(user.getId())) {
                throw new IllegalArgumentException("This account already exists");
            }
        }

        if (finalAccountNumber != null) {
            boolean exists = accountGateway.existsByAccountNumberAndAccountType(finalAccountNumber, type);

            if (exists) {
                throw new IllegalArgumentException(
                        String.format("An account of type %s already exists with the number %s. Please choose another", type, finalAccountNumber)
                );
            }
        }

        BigDecimal finalBalance = BigDecimal.ZERO;
        BigDecimal finalCreditLimit = BigDecimal.ZERO;

        if (type == AccountType.CARTAO_CREDITO) {
            finalBalance = BigDecimal.ZERO;
            if (creditLimitDto != null && creditLimitDto.compareTo(BigDecimal.ZERO) > 0) {
                finalCreditLimit = creditLimitDto;
            } else {
                throw new IllegalArgumentException("Card limit is required");
            }
        } else if (type == AccountType.BANCO) {
            finalBalance = bankIntegrationGateway.getBalanceFromExternalBank(finalAccountNumber);
        } else {
            finalBalance = (initialBalance != null) ? initialBalance : BigDecimal.ZERO;
        }

        Account account = new Account(
                user.getId(), name, type, finalAccountNumber, finalBalance, finalCreditLimit
        );

        accountGateway.create(account);
    }

}
