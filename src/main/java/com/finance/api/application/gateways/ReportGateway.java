package com.finance.api.application.gateways;

import com.finance.api.domain.transaction.Transaction;

import java.util.List;

public interface ReportGateway {
    byte[] generateExcel(List<Transaction> transactions);
    byte[] generatePdf(List<Transaction> transactions, String userName);
}
