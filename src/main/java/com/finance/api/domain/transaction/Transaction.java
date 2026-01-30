package com.finance.api.domain.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private Long accountId;
    private Long categoryId;
    private BigDecimal amount;
    private String currency;
    private BigDecimal quotation;
    private BigDecimal finalAmount;
    private TransactionType type;
    private String description;
    private LocalDateTime date;

    public Transaction(Long accountId, Long categoryId, BigDecimal amount, String currency, TransactionType type, String description, LocalDate dateQuotation) {
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.currency = (currency != null) ? currency.toUpperCase() : "BRL";
        this.type = type;
        this.description = description;
        this.quotation = BigDecimal.ONE;
        this.finalAmount = amount;

        if (dateQuotation != null) {
            this.date = dateQuotation.atTime(java.time.LocalTime.now());
        } else {
            this.date = LocalDateTime.now();
        }
    }

    public void applyQuotation(BigDecimal rate) {
        this.quotation = rate;
        this.finalAmount = this.amount.multiply(rate);
    }

    public void setId(Long id) { this.id = id; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public void setQuotation(BigDecimal quotation) { this.quotation = quotation; }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public Long getCategoryId() { return categoryId; }
    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public BigDecimal getQuotation() { return quotation; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public TransactionType getType() { return type; }
    public String getDescription() { return description; }
    public LocalDateTime getDate() { return date; }
}
