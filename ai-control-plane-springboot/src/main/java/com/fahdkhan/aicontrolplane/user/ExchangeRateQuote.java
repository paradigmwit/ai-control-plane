package com.fahdkhan.aicontrolplane.user;

import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeRateQuote(
        BigDecimal exchangeRate,
        String rateSource,
        Instant rateTimestamp,
        BigDecimal confidence,
        String version,
        boolean stale) {

    public ExchangeRateQuote withStale(boolean staleValue) {
        return new ExchangeRateQuote(exchangeRate, rateSource, rateTimestamp, confidence, version, staleValue);
    }
}
