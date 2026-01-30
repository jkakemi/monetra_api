package com.finance.api.infra.controller;

import com.finance.api.application.usecases.account.CreateAccount;
import com.finance.api.application.usecases.account.DeleteAccount;
import com.finance.api.application.usecases.account.ListMyAccounts;
import com.finance.api.application.usecases.account.UpdateAccount;
import com.finance.api.domain.account.Account;
import com.finance.api.infra.controller.dto.AccountRequestDTO;
import com.finance.api.infra.controller.dto.AccountResponseDTO;
import com.finance.api.infra.controller.dto.AccountUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CreateAccount createAccount;
    private final ListMyAccounts listMyAccounts;
    private final DeleteAccount deleteAccount;
    private final UpdateAccount updateAccount;

    public AccountController(CreateAccount createAccount, ListMyAccounts listMyAccounts, DeleteAccount deleteAccount, UpdateAccount updateAccount) {
        this.createAccount = createAccount;
        this.listMyAccounts = listMyAccounts;
        this.deleteAccount = deleteAccount;
        this.updateAccount = updateAccount;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid AccountRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        createAccount.execute(
                email,
                dto.name(),
                dto.type(),
                dto.accountNumber(),
                dto.balance(),
                dto.creditLimit()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> list() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Account> accounts = listMyAccounts.execute(email);

        List<AccountResponseDTO> response = accounts.stream()
                .map(acc -> new AccountResponseDTO(
                        acc.getId(),
                        acc.getName(),
                        acc.getType().toString(),
                        acc.getAccountNumber(),
                        acc.getBalance(),
                        acc.getCreditLimit()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody AccountUpdateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        updateAccount.execute(email, id, dto.name(), dto.creditLimit());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        deleteAccount.execute(email, id);
        return ResponseEntity.noContent().build();
    }
}
