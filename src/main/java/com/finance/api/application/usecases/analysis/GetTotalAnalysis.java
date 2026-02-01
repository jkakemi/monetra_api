package com.finance.api.application.usecases.analysis;

import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.user.User;
import com.finance.api.infra.controller.dto.TotalExpenseResponseDTO;

import java.math.BigDecimal;

public class GetTotalAnalysis {

    private final TransactionGateway transactionGateway;
    private final UserGateway userGateway;

    public GetTotalAnalysis(TransactionGateway transactionGateway, UserGateway userGateway) {
        this.transactionGateway = transactionGateway;
        this.userGateway = userGateway;
    }

    public TotalExpenseResponseDTO execute(String userEmail, int month, int year) {
        User user = userGateway.findByEmail(userEmail);

        BigDecimal totalExpenses = transactionGateway.totalExpenses(user.getId(), month, year);
        BigDecimal totalIncome = transactionGateway.totalIncome(user.getId(), month, year);
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        return new TotalExpenseResponseDTO(totalExpenses, totalIncome, balance);
    }
}
