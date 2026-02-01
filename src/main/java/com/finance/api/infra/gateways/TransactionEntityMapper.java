package com.finance.api.infra.gateways;

import com.finance.api.domain.transaction.Transaction;
import com.finance.api.infra.persistence.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionEntityMapper {

    public TransactionEntity toEntity(Transaction domain) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(domain.getId());
        entity.setAmount(domain.getAmount());
        entity.setCurrency(domain.getCurrency());
        entity.setExchangeRate(domain.getQuotation());
        entity.setFinalAmount(domain.getFinalAmount());
        entity.setType(domain.getType());
        entity.setStatus(domain.getStatus());
        entity.setDescription(domain.getDescription());

        entity.setDate(domain.getDate());

        return entity;
    }

    public Transaction toDomain(TransactionEntity entity) {
        Long accountId = (entity.getAccount() != null) ? entity.getAccount().getId() : null;
        Long categoryId = (entity.getCategory() != null) ? entity.getCategory().getId() : null;

        Transaction transaction = new Transaction(
                accountId,
                categoryId,
                entity.getAmount(),
                entity.getCurrency(),
                entity.getType(),
                entity.getDescription(),
                entity.getDate().toLocalDate()
        );

        transaction.setId(entity.getId());
        transaction.setFinalAmount(entity.getFinalAmount());
        transaction.setQuotation(entity.getExchangeRate());
        transaction.setStatus(entity.getStatus());
        transaction.setDate(entity.getDate());

        return transaction;
    }
}