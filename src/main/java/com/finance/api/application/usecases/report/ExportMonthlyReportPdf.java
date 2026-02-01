package com.finance.api.application.usecases.report;

import com.finance.api.application.gateways.AccountGateway;
import com.finance.api.application.gateways.ReportGateway;
import com.finance.api.application.gateways.TransactionGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.account.Account;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.user.User;

import java.util.ArrayList;
import java.util.List;

public class ExportMonthlyReportPdf {
    private final TransactionGateway transactionGateway;
    private final AccountGateway accountGateway;
    private final UserGateway userGateway;
    private final ReportGateway reportGateway;

    public ExportMonthlyReportPdf(TransactionGateway transactionGateway, AccountGateway accountGateway, UserGateway userGateway, ReportGateway reportGateway) {
        this.transactionGateway = transactionGateway;
        this.accountGateway = accountGateway;
        this.userGateway = userGateway;
        this.reportGateway = reportGateway;
    }

    public byte[] execute(String userEmail) {
        User user = userGateway.findByEmail(userEmail);

        List<Account> accounts = accountGateway.listByUserId(user.getId());

        List<Transaction> allTransactions = new ArrayList<>();
        for (Account acc : accounts) {
            allTransactions.addAll(transactionGateway.listByAccountId(acc.getId()));
        }

        if (allTransactions.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found to export");
        }

        return reportGateway.generatePdf(allTransactions, user.getName());
    }
}
