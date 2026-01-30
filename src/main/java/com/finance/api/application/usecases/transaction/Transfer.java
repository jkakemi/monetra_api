package com.finance.api.application.usecases.transaction;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.CategoryGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.account.AccountType;
import com.finance.api.domain.category.Category;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.transaction.TransactionType;
import com.finance.api.domain.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transfer {
    private final AccountGateway accountGateway;
    private final TransactionGateway transactionGateway;
    private final UserGateway userGateway;
    private final CategoryGateway categoryGateway;

    public Transfer(AccountGateway accountGateway, TransactionGateway transactionGateway, UserGateway userGateway, CategoryGateway categoryGateway) {
        this.accountGateway = accountGateway;
        this.transactionGateway = transactionGateway;
        this.userGateway = userGateway;
        this.categoryGateway = categoryGateway;
    }

    @Transactional
    public void execute(String userEmail, Long sourceAccountId, String targetAccountNumber, String targetTypeAccount, BigDecimal amount, String description, LocalDate customDate, Long userCategoryId) {
        User sender = userGateway.findByEmail(userEmail);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Account sourceAccount = accountGateway.findById(sourceAccountId);

        if (!sourceAccount.getUserId().equals(sender.getId())) {
            throw new IllegalArgumentException("Source account does not belong to this account");
        }

        BigDecimal balance = sourceAccount.getBalance().add(sourceAccount.getCreditLimit());
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência/pagamento.");
        }

        Account targetAccount = accountGateway.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Target account not found: " + targetAccountNumber));

        if (sourceAccount.getId().equals(targetAccount.getId())) {
            throw new IllegalArgumentException("The source and target accounts cannot be the same account.");
        }

        if (userCategoryId != null) {
            Category category = categoryGateway.findById(userCategoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));

            if (!category.getUserId().equals(sender.getId())) {
                throw new IllegalArgumentException("This category does not exist");
            }
        }

        if (targetTypeAccount == null || targetTypeAccount.isBlank()) {
            throw new IllegalArgumentException("Target account type is necessary (BANCO, FISICO)");
        }

        AccountType targetType;
        try {
            targetType = AccountType.valueOf(targetTypeAccount.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid target account type");
        }

        Account targetAccount2 = accountGateway.findByAccountNumberAndAccountType(targetAccountNumber, targetType)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Destination account '%s' of type '%s' not found", targetAccountNumber, targetType)
                ));

        if (sourceAccount.getId().equals(targetAccount2.getId())) {
            throw new IllegalArgumentException("The source and target accounts cannot be the same account");
        }

        LocalDate date = (customDate != null) ? customDate : LocalDate.now();

        Transaction debitTransaction = new Transaction(
                sourceAccount.getId(),
                userCategoryId,
                amount,
                "BRL",
                TransactionType.TRANSFERENCIA,
                "Send to: " + targetAccount.getName() + " - " + description,
                date
        );

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));

        Transaction creditTransaction = new Transaction(
                targetAccount.getId(),
                null,
                amount,
                "BRL",
                TransactionType.TRANSFERENCIA,
                "Received from: " + sourceAccount.getName() + " - " + description,
                date
        );

        targetAccount.setBalance(targetAccount.getBalance().add(amount));

        accountGateway.update(sourceAccount);
        accountGateway.update(targetAccount);
        transactionGateway.create(debitTransaction);
        transactionGateway.create(creditTransaction);
    }
}