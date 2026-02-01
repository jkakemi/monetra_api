package com.finance.api.infra.controller;

import com.finance.api.application.usecases.report.ExportMonthlyReport;
import com.finance.api.application.usecases.report.ExportMonthlyReportPdf;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger; // Importante
import org.slf4j.LoggerFactory;
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
    private final ExportMonthlyReportPdf exportMonthlyReportPdf;
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);



    public ReportController(ExportMonthlyReport exportMonthlyReport, ExportMonthlyReportPdf exportMonthlyReportPdf) {
        this.exportMonthlyReport = exportMonthlyReport;
        this.exportMonthlyReportPdf = exportMonthlyReportPdf;
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

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadPdf() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] pdfFile = exportMonthlyReportPdf.execute(email);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=finance_extract.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfFile);
    }
}