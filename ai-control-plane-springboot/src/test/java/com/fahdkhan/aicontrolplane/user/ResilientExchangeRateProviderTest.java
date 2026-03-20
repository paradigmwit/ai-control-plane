package com.fahdkhan.aicontrolplane.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ResilientExchangeRateProviderTest {

    @Test
    void shouldReturnProviderRateWhenSuccessful() {
        ExchangeRateProvider delegate = mock(ExchangeRateProvider.class);
        when(delegate.resolveRate("USD", "EUR")).thenReturn(new ExchangeRateQuote(
                new BigDecimal("0.93"),
                "primary-api",
                Instant.parse("2026-01-02T10:00:00Z"),
                new BigDecimal("0.98"),
                "api-v1",
                false));

        ResilientExchangeRateProvider provider = new ResilientExchangeRateProvider(delegate);

        ExchangeRateQuote quote = provider.resolveRate("USD", "EUR");

        assertEquals(new BigDecimal("0.93"), quote.exchangeRate());
        assertEquals("primary-api", quote.rateSource());
        assertFalse(quote.stale());
        verify(delegate, times(1)).resolveRate("USD", "EUR");
    }

    @Test
    void shouldFallbackToCachedRateWhenProviderFails() {
        ExchangeRateProvider delegate = mock(ExchangeRateProvider.class);
        when(delegate.resolveRate("USD", "EUR"))
                .thenReturn(new ExchangeRateQuote(
                        new BigDecimal("0.92"),
                        "primary-api",
                        Instant.parse("2026-01-02T10:00:00Z"),
                        new BigDecimal("0.97"),
                        "api-v1",
                        false))
                .thenThrow(new IllegalStateException("upstream unavailable"));

        ResilientExchangeRateProvider provider = new ResilientExchangeRateProvider(delegate);

        ExchangeRateQuote first = provider.resolveRate("USD", "EUR");
        ExchangeRateQuote second = provider.resolveRate("USD", "EUR");

        assertFalse(first.stale());
        assertTrue(second.stale());
        assertEquals(first.exchangeRate(), second.exchangeRate());
        verify(delegate, times(4)).resolveRate("USD", "EUR");
    }

    @Test
    void shouldFailWhenProviderFailsAndNoCacheExists() {
        ExchangeRateProvider delegate = mock(ExchangeRateProvider.class);
        when(delegate.resolveRate("USD", "EUR")).thenThrow(new IllegalStateException("upstream unavailable"));

        ResilientExchangeRateProvider provider = new ResilientExchangeRateProvider(delegate);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> provider.resolveRate("USD", "EUR"));

        assertTrue(exception.getMessage().contains("no cached fallback"));
        verify(delegate, times(3)).resolveRate("USD", "EUR");
    }
}
