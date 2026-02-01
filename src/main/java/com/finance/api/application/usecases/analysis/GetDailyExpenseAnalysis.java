package com.finance.api.application.usecases.analysis;

import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.user.User;
import com.finance.api.infra.controller.dto.DailyExpenseResponseDTO;

import java.util.List;

public class GetDailyExpenseAnalysis {

    private final TransactionGateway transactionGateway;
    private final UserGateway userGateway;

    public GetDailyExpenseAnalysis(TransactionGateway transactionGateway, UserGateway userGateway) {
        this.transactionGateway = transactionGateway;
        this.userGateway = userGateway;
    }

    public List<DailyExpenseResponseDTO> execute(String userEmail, int month, int year) {
        User user = userGateway.findByEmail(userEmail);
        return transactionGateway.dailyExpenses(user.getId(), month, year);
    }
}
