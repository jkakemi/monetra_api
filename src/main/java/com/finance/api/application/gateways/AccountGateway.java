package com.finance.api.application.gateways;

import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;

import java.util.List;
import java.util.Optional;

public interface AccountGateway {
    Account create(Account account);
    void update(Account account);
    List<Account> listByUserId(Long userId);
    void delete(Long id);
    Account findById(Long id);
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    boolean existsByAccountNumberAndAccountType(String accountNumber, AccountType type);
    Optional<Account> findByAccountNumberAndAccountType(String accountNumber, AccountType type);
}
