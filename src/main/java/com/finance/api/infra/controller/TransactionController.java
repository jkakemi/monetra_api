package com.finance.api.infra.controller;

import com.finance.api.application.usecases.transaction.CreateTransaction;
import com.finance.api.application.usecases.transaction.DeleteTransaction;
import com.finance.api.application.usecases.transaction.ListTransactions;
import com.finance.api.application.usecases.transaction.Transfer;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.infra.controller.dto.TransactionRequestDTO;
import com.finance.api.infra.controller.dto.TransactionResponseDTO;
import com.finance.api.infra.controller.dto.TransferRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransaction createTransaction;
    private final ListTransactions listTransactions;
    private final DeleteTransaction deleteTransaction;
    private final Transfer transferTransaction;

    public TransactionController(CreateTransaction createTransaction, ListTransactions listTransactions, DeleteTransaction deleteTransaction, Transfer transferTransaction) {
        this.createTransaction = createTransaction;
        this.listTransactions = listTransactions;
        this.deleteTransaction = deleteTransaction;
        this.transferTransaction = transferTransaction;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody @Valid TransactionRequestDTO dto) {
        String currency = (dto.currency() != null) ? dto.currency() : "BRL";

        Transaction transaction = createTransaction.execute(
                dto.accountId(),
                dto.categoryId(),
                dto.amount(),
                currency,
                dto.type(),
                dto.description(),
                dto.date()
        );

        TransactionResponseDTO response = new TransactionResponseDTO(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getType().toString(),
                transaction.getStatus().toString(),
                transaction.getDescription(),
                transaction.getDate().toLocalDate()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> list(@RequestParam Long accountId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Transaction> transactions = listTransactions.execute(email, accountId);

        List<TransactionResponseDTO> response = transactions.stream()
                .map(t -> new TransactionResponseDTO(
                        t.getId(),
                        t.getAccountId(),
                        t.getCategoryId(),
                        t.getAmount(),
                        t.getCurrency(),
                        t.getType().toString(),
                        t.getStatus() != null ? t.getStatus().toString() : "PENDING",
                        t.getDescription(),
                        t.getDate().toLocalDate()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        deleteTransaction.execute(email, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        transferTransaction.execute(
                email,
                dto.sourceAccountId(),
                dto.targetAccountNumber(),
                dto.targetAccountType(),
                dto.amount(),
                dto.description(),
                dto.date(),
                dto.categoryId()
        );

        return ResponseEntity.ok().build();
    }
}