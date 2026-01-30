package com.finance.api.domain.category;

import com.finance.api.domain.transaction.TransactionType;

public class Category {
    private Long id;
    private String name;
    private TransactionType type;
    private Long userId;

    public Category(Long id, String name, TransactionType type, Long userId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.userId = userId;
    }

    public void update(String name, TransactionType type) {
        if (name != null && !name.isBlank()) this.name = name;
        if (type != null) this.type = type;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public TransactionType getType() { return type; }
    public Long getUserId() { return userId; }

    public void setId(Long id) { this.id = id; }
}