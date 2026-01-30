package com.finance.api.infra.persistence;

import com.finance.api.infra.controller.dto.ExpenseAnalysisResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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

}
