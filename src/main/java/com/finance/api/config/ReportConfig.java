package com.finance.api.config;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.ReportGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.application.usecases.report.ExportMonthlyReport;
import com.finance.api.application.usecases.report.ExportMonthlyReportPdf;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportConfig {
    @Bean
    ExportMonthlyReport exportMonthlyReport(
            TransactionGateway transactionGateway,
            AccountGateway accountGateway,
            UserGateway userGateway,
            ReportGateway reportGateway) {
        return new ExportMonthlyReport(transactionGateway, accountGateway, userGateway, reportGateway);
    }

    @Bean
    ExportMonthlyReportPdf exportMonthlyReportPdf(
            TransactionGateway transactionGateway,
            AccountGateway accountGateway,
            UserGateway userGateway,
            ReportGateway reportGateway) {
        return new ExportMonthlyReportPdf(transactionGateway, accountGateway, userGateway, reportGateway);
    }
}
