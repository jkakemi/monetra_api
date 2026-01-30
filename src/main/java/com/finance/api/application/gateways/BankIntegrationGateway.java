package com.finance.api.application.gateways;

import java.math.BigDecimal;

public interface BankIntegrationGateway {
    BigDecimal getBalanceFromExternalBank(String accountNumber);
}
