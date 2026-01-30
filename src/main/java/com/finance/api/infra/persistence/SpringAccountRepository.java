package com.finance.api.infra.persistence;

import com.finance.api.domain.account.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringAccountRepository extends JpaRepository<AccountEntity, Long> {
    List<AccountEntity> findByUserId(Long userId);
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    boolean existsByAccountNumberAndAccountType(String accountNumber, AccountType accountType);
    Optional<AccountEntity> findByAccountNumberAndAccountType(String accountNumber, AccountType accountType);
}
