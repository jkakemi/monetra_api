package com.finance.api.infra.gateways;

import com.finance.api.domain.account.Account;
import com.finance.api.infra.persistence.AccountEntity;

public class AccountEntityMapper {
    public AccountEntity toEntity(Account accountDomain) {
        AccountEntity entity = new AccountEntity();
        entity.setName(accountDomain.getName());
        entity.setAccountNumber(accountDomain.getAccountNumber());
        entity.setBalance(accountDomain.getBalance());
        entity.setCreditLimit(accountDomain.getCreditLimit());
        entity.setAccountType(accountDomain.getType());

        return entity;
    }
    public Account toDomain(AccountEntity entity) {
        Long userId = (entity.getUser() != null) ? entity.getUser().getId() : null;

        Account domain = new Account(
                userId,
                entity.getName(),
                entity.getAccountType(),
                entity.getAccountNumber(),
                entity.getBalance(),
                entity.getCreditLimit()
        );

        domain.setId(entity.getId());
        return domain;
    }
}