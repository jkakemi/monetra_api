package com.finance.api.application.gateways;

import com.finance.api.domain.transaction.Transaction;
import com.finance.api.infra.controller.dto.ExpenseAnalysisResponseDTO;

import java.util.List;
import java.util.Optional;

public interface TransactionGateway {
    Transaction create(Transaction transaction);
    List<Transaction> listByAccountId(Long accountId);
    boolean existsByAccountId(Long accountId);
    Optional<Transaction> findById(Long id);
    void delete(Long id);
    List<ExpenseAnalysisResponseDTO> expenseAnalysis(Long userId, int month, int year);


}
