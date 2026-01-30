package com.finance.api.infra.controller;

import com.finance.api.application.usecases.report.ExportMonthlyReport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ExportMonthlyReport exportMonthlyReport;

    public ReportController(ExportMonthlyReport exportMonthlyReport) {
        this.exportMonthlyReport = exportMonthlyReport;
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadExcel() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] excelFile = exportMonthlyReport.execute(email);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=finance_extract.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
    }
}