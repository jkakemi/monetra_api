package com.finance.api.infra.controller;

import com.finance.api.application.usecases.analysis.GetMonthlyExpenseAnalysis;
import com.finance.api.infra.controller.dto.ExpenseAnalysisResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final GetMonthlyExpenseAnalysis getMonthlyExpenseAnalysis;

    public AnalysisController(GetMonthlyExpenseAnalysis getMonthlyExpenseAnalysis) {
        this.getMonthlyExpenseAnalysis = getMonthlyExpenseAnalysis;
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<ExpenseAnalysisResponseDTO>> getMonthlyExpenses(
            @RequestParam int month,
            @RequestParam int year
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ExpenseAnalysisResponseDTO> result = getMonthlyExpenseAnalysis.execute(email, month, year);

        return ResponseEntity.ok(result);
    }
}