package com.finance.api.application.usecases.analysis;

import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.user.User;
import com.finance.api.infra.controller.dto.ExpenseAnalysisResponseDTO;

import java.util.List;

public class GetMonthlyExpenseAnalysis {

    private final TransactionGateway transactionGateway;
    private final UserGateway userGateway;

    public GetMonthlyExpenseAnalysis(TransactionGateway transactionGateway, UserGateway userGateway) {
        this.transactionGateway = transactionGateway;
        this.userGateway = userGateway;
    }

    public List<ExpenseAnalysisResponseDTO> execute(String userEmail, int month, int year) {
        User user = userGateway.findByEmail(userEmail);

        return transactionGateway.expenseAnalysis(user.getId(), month, year);
    }
}