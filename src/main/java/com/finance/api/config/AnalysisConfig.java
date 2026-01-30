package com.finance.api.config;

import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.application.usecases.analysis.GetMonthlyExpenseAnalysis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalysisConfig {
    @Bean
    GetMonthlyExpenseAnalysis getMonthlyExpenseAnalysis(TransactionGateway transactionGateway, UserGateway userGateway) {
        return new GetMonthlyExpenseAnalysis(transactionGateway, userGateway);
    }
}
