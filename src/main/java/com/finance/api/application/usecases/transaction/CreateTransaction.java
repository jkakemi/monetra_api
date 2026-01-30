package com.finance.api.application.usecases.transaction;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.CategoryGateway; // Crie essa interface simples (findById)
import com.finance.api.application.gateways.QuoteGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.category.Category;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTransaction {
    private final TransactionGateway transactionGateway;
    private final AccountGateway accountGateway;
    private final QuoteGateway quoteGateway;
    private final CategoryGateway categoryGateway;

    public CreateTransaction(TransactionGateway transactionGateway, AccountGateway accountGateway, QuoteGateway quoteGateway, CategoryGateway categoryGateway) {
        this.transactionGateway = transactionGateway;
        this.accountGateway = accountGateway;
        this.quoteGateway = quoteGateway;
        this.categoryGateway = categoryGateway;
    }

    public void execute(Long accountId, Long categoryId, BigDecimal amount, String currency, String typeCategory, String description, LocalDate transactionDate) {
        LocalDate date = (transactionDate != null) ? transactionDate : LocalDate.now();

        Account account = accountGateway.findById(accountId);

        if (categoryId != null) {
            Category category = categoryGateway.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));
            if (category.getType() != TransactionType.valueOf(typeCategory)) {
                throw new IllegalArgumentException("Invalid category type");
            }
        }

        TransactionType type = TransactionType.valueOf(typeCategory.toUpperCase());

        Transaction transaction = new Transaction(accountId, categoryId, amount, currency, type, description, date);

        if (!"BRL".equalsIgnoreCase(currency)) {
            BigDecimal rate = quoteGateway.getQuote(currency, date);
            transaction.applyQuotation(rate);
        }

        BigDecimal finalValue = transaction.getFinalAmount();

        if (type == TransactionType.RECEITA) {
            account.setBalance(account.getBalance().add(finalValue));
        } else {
            BigDecimal totalAmount = account.getBalance().add(account.getCreditLimit());
            if (totalAmount.compareTo(finalValue) < 0) {
                throw new IllegalArgumentException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(finalValue));
        }

        accountGateway.update(account);
        transactionGateway.create(transaction);
    }
}