package com.fahdkhan.aicontrolplane.user;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("internalFeedExchangeRateProvider")
public class InternalFeedExchangeRateProvider implements ExchangeRateProvider {

    private static final Map<String, BigDecimal> USD_VALUE_BY_CURRENCY = Map.of(
            "USD", BigDecimal.ONE,
            "EUR", new BigDecimal("1.08"),
            "GBP", new BigDecimal("1.27"),
            "JPY", new BigDecimal("0.0067"),
            "INR", new BigDecimal("0.012"),
            "PKR", new BigDecimal("0.0036"));

    @Override
    public ExchangeRateQuote resolveRate(String sourceCurrency, String targetCurrency) {
        BigDecimal sourceInUsd = USD_VALUE_BY_CURRENCY.get(sourceCurrency);
        BigDecimal targetInUsd = USD_VALUE_BY_CURRENCY.get(targetCurrency);

        if (sourceInUsd == null || targetInUsd == null) {
            throw new IllegalArgumentException("Only USD, EUR, GBP, JPY, INR, and PKR are supported.");
        }

        BigDecimal rate = sourceInUsd.divide(targetInUsd, 8, RoundingMode.HALF_UP);
        return new ExchangeRateQuote(
                rate,
                "internal-feed",
                Instant.now(),
                BigDecimal.ONE,
                "internal-feed-v1",
                false);
    }
}
