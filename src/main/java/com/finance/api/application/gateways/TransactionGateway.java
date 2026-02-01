package com.finance.api.application.gateways;

import com.finance.api.domain.transaction.Transaction;
import com.finance.api.infra.controller.dto.DailyExpenseResponseDTO;
import com.finance.api.infra.controller.dto.ExpenseAnalysisResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionGateway {
    Transaction create(Transaction transaction);
    Transaction update(Transaction transaction);
    List<Transaction> listByAccountId(Long accountId);
    boolean existsByAccountId(Long accountId);
    Optional<Transaction> findById(Long id);
    void delete(Long id);
    List<ExpenseAnalysisResponseDTO> expenseAnalysis(Long userId, int month, int year);
    List<DailyExpenseResponseDTO> dailyExpenses(Long userId, int month, int year);
    BigDecimal totalExpenses(Long userId, int month, int year);
    BigDecimal totalIncome(Long userId, int month, int year);
}
