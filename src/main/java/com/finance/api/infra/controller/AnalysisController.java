package com.finance.api.infra.controller;

import com.finance.api.application.usecases.analysis.GetDailyExpenseAnalysis;
import com.finance.api.application.usecases.analysis.GetMonthlyExpenseAnalysis;
import com.finance.api.application.usecases.analysis.GetTotalAnalysis;
import com.finance.api.infra.controller.dto.DailyExpenseResponseDTO;
import com.finance.api.infra.controller.dto.ExpenseAnalysisResponseDTO;
import com.finance.api.infra.controller.dto.TotalExpenseResponseDTO;
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
    private final GetDailyExpenseAnalysis getDailyExpenseAnalysis;
    private final GetTotalAnalysis getTotalAnalysis;

    public AnalysisController(
            GetMonthlyExpenseAnalysis getMonthlyExpenseAnalysis,
            GetDailyExpenseAnalysis getDailyExpenseAnalysis,
            GetTotalAnalysis getTotalAnalysis) {
        this.getMonthlyExpenseAnalysis = getMonthlyExpenseAnalysis;
        this.getDailyExpenseAnalysis = getDailyExpenseAnalysis;
        this.getTotalAnalysis = getTotalAnalysis;
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

    @GetMapping("/daily")
    public ResponseEntity<List<DailyExpenseResponseDTO>> getDailyExpenses(
            @RequestParam int month,
            @RequestParam int year
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<DailyExpenseResponseDTO> result = getDailyExpenseAnalysis.execute(email, month, year);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/total")
    public ResponseEntity<TotalExpenseResponseDTO> getTotalAnalysis(
            @RequestParam int month,
            @RequestParam int year
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        TotalExpenseResponseDTO result = getTotalAnalysis.execute(email, month, year);
        return ResponseEntity.ok(result);
    }
}