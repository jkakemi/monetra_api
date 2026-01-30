package com.finance.api.application.gateways;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface QuoteGateway {
    BigDecimal getQuote(String coinType, LocalDate date);
}
