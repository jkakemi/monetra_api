package com.finance.api.infra.kafka;

import java.math.BigDecimal;

public record TransactionEvent(
    Long transactionId,
    Long accountId,
    Long userId,
    BigDecimal amount,
    String currency,
    String type
) {}
