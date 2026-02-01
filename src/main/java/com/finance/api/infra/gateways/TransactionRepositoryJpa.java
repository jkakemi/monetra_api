package com.finance.api.infra.gateways;

import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.infra.controller.dto.DailyExpenseResponseDTO;
import com.finance.api.infra.controller.dto.ExpenseAnalysisResponseDTO;
import com.finance.api.infra.persistence.SpringAccountRepository;
import com.finance.api.infra.persistence.SpringCategoryRepository;
import com.finance.api.infra.persistence.SpringTransactionRepository;
import com.finance.api.infra.persistence.TransactionEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TransactionRepositoryJpa implements TransactionGateway {

    private final SpringTransactionRepository springTransactionRepository;
    private final SpringAccountRepository springAccountRepository;
    private final SpringCategoryRepository springCategoryRepository;
    private final TransactionEntityMapper mapper;

    public TransactionRepositoryJpa(SpringTransactionRepository springTransactionRepository,
                                    SpringAccountRepository springAccountRepository,
                                    TransactionEntityMapper mapper,
                                    SpringCategoryRepository springCategoryRepository) {
        this.springTransactionRepository = springTransactionRepository;
        this.springAccountRepository = springAccountRepository;
        this.mapper = mapper;
        this.springCategoryRepository = springCategoryRepository;
    }

    @Override
    public Transaction create(Transaction domain) {
        TransactionEntity entity = mapper.toEntity(domain);

        var accountEntity = springAccountRepository.findById(domain.getAccountId()).orElseThrow();
        entity.setAccount(accountEntity);

        if (domain.getCategoryId() != null) {
            var categoryEntity = springCategoryRepository.findById(domain.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            entity.setCategory(categoryEntity);
        }

        springTransactionRepository.save(entity);
        domain.setId(entity.getId());
        return domain;
    }

    @Override
    public Transaction update(Transaction domain) {
        TransactionEntity entity = springTransactionRepository.findById(domain.getId())
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        entity.setStatus(domain.getStatus());
        springTransactionRepository.save(entity);
        return domain;
    }

    @Override
    public List<Transaction> listByAccountId(Long accountId) {
        return springTransactionRepository.findByAccountId(accountId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAccountId(Long accountId) {
        return springTransactionRepository.existsByAccountId(accountId);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return springTransactionRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void delete(Long id) {
        if (springTransactionRepository.existsById(id)) {
            springTransactionRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Transaction not found");
        }
    }

    @Override
    public List<ExpenseAnalysisResponseDTO> expenseAnalysis(Long userId, int month, int year) {
        return springTransactionRepository.findExpensesByCategory(userId, month, year);
    }

    @Override
    public List<DailyExpenseResponseDTO> dailyExpenses(Long userId, int month, int year) {
        return springTransactionRepository.findDailyExpenses(userId, month, year);
    }

    @Override
    public BigDecimal totalExpenses(Long userId, int month, int year) {
        return springTransactionRepository.sumExpensesByUserAndMonth(userId, month, year);
    }

    @Override
    public BigDecimal totalIncome(Long userId, int month, int year) {
        return springTransactionRepository.sumIncomeByUserAndMonth(userId, month, year);
    }
}