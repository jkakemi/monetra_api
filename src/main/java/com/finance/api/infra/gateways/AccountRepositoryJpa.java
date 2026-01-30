package com.finance.api.infra.gateways;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;
import com.finance.api.infra.persistence.AccountEntity;
import com.finance.api.infra.persistence.SpringAccountRepository;
import com.finance.api.infra.persistence.SpringUserRepository;
import com.finance.api.infra.persistence.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AccountRepositoryJpa implements AccountGateway {
    private final SpringAccountRepository accountRepository;
    private final SpringUserRepository userRepository;
    private final AccountEntityMapper mapper;

    public AccountRepositoryJpa(SpringAccountRepository accountRepository,
                                SpringUserRepository userRepository,
                                AccountEntityMapper mapper) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Account create(Account domain) {
        AccountEntity entity = mapper.toEntity(domain);

        UserEntity user = userRepository.findById(domain.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        entity.setUser(user);

        AccountEntity savedEntity = accountRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public void update(Account account) {
        AccountEntity entity = accountRepository.findById(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        entity.setName(account.getName());
        entity.setBalance(account.getBalance());
        entity.setCreditLimit(account.getCreditLimit());
        // entity.setType(account.getType());

        accountRepository.save(entity);
    }

    @Override
    public List<Account> listByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {

        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Account not found");
        }
    }


    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public boolean existsByAccountNumberAndAccountType(String accountNumber, AccountType type) {
        return accountRepository.existsByAccountNumberAndAccountType(accountNumber, type);
    }

    @Override
    public Optional<Account> findByAccountNumberAndAccountType(String accountNumber, AccountType type) {
        return accountRepository.findByAccountNumberAndAccountType(accountNumber, type)
                .map(mapper::toDomain);
    }


}
