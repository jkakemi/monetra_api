package com.finance.api.infra.persistence;

import com.finance.api.infra.controller.dto.DailyExpenseResponseDTO;
import com.finance.api.infra.controller.dto.ExpenseAnalysisResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SpringTransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByAccountId(Long accountId);
    boolean existsByAccountId(Long accountId);

    @Query("""
        SELECT new com.finance.api.infra.controller.dto.ExpenseAnalysisResponseDTO(
            c.name,
            SUM(t.finalAmount)
        )
        FROM TransactionEntity t
        JOIN t.category c
        JOIN t.account a
        WHERE a.user.id = :userId
          AND MONTH(t.date) = :month
          AND YEAR(t.date) = :year
          AND t.type = 'DESPESA'
        GROUP BY c.name
    """)
    List<ExpenseAnalysisResponseDTO> findExpensesByCategory(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
        SELECT new com.finance.api.infra.controller.dto.DailyExpenseResponseDTO(
            CAST(t.date AS LocalDate),
            SUM(t.finalAmount)
        )
        FROM TransactionEntity t
        JOIN t.account a
        WHERE a.user.id = :userId
          AND MONTH(t.date) = :month
          AND YEAR(t.date) = :year
          AND t.type = 'DESPESA'
        GROUP BY CAST(t.date AS LocalDate)
        ORDER BY CAST(t.date AS LocalDate)
    """)
    List<DailyExpenseResponseDTO> findDailyExpenses(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
        SELECT COALESCE(SUM(t.finalAmount), 0)
        FROM TransactionEntity t
        JOIN t.account a
        WHERE a.user.id = :userId
          AND MONTH(t.date) = :month
          AND YEAR(t.date) = :year
          AND t.type = 'DESPESA'
    """)
    BigDecimal sumExpensesByUserAndMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
        SELECT COALESCE(SUM(t.finalAmount), 0)
        FROM TransactionEntity t
        JOIN t.account a
        WHERE a.user.id = :userId
          AND MONTH(t.date) = :month
          AND YEAR(t.date) = :year
          AND t.type = 'RECEITA'
    """)
    BigDecimal sumIncomeByUserAndMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );
}
