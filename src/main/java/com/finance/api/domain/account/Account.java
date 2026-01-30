package com.finance.api.domain.account;

import java.math.BigDecimal;

public class Account {

    private Long id;
    private String name;
    private String accountNumber;
    private AccountType type;
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private Long userId;

    public Account(Long userId, String name, AccountType type, String accountNumber, BigDecimal balance, BigDecimal creditLimit){
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Account name is required");
        }
        if(type == null){
            throw new IllegalArgumentException("Account type is required");
        }
//        if(accountNumber == null || accountNumber.isBlank()){
//            throw new IllegalArgumentException("Account number is required");
//        }

        this.userId = userId;
        this.name = name;
        this.type = type;
        this.accountNumber = accountNumber;
        this.balance = (balance != null) ? balance: BigDecimal.ZERO;
        this.creditLimit = (creditLimit != null) ? creditLimit: BigDecimal.ZERO;
    }

    public void update(String name, BigDecimal creditLimit) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }

        if (creditLimit != null) {
            this.creditLimit = creditLimit;
        }
    }


    public void setId(Long id) {
        this.id = id;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public AccountType getType() {
        return type;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }
    public Long getUserId() {
        return userId;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
}
