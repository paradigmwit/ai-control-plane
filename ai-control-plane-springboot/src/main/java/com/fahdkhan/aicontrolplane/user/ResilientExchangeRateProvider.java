package com.fahdkhan.aicontrolplane.user;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ResilientExchangeRateProvider implements ExchangeRateProvider {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration REQUEST_TIMEOUT = Duration.ofMillis(250);

    private final ExchangeRateProvider delegate;
    private final Map<String, ExchangeRateQuote> cachedRatesByPair = new ConcurrentHashMap<>();

    public ResilientExchangeRateProvider(
            @Qualifier("internalFeedExchangeRateProvider") ExchangeRateProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public ExchangeRateQuote resolveRate(String sourceCurrency, String targetCurrency) {
        RuntimeException lastFailure = null;
        String cacheKey = buildCacheKey(sourceCurrency, targetCurrency);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                ExchangeRateQuote quote = executeWithTimeout(sourceCurrency, targetCurrency);
                cachedRatesByPair.put(cacheKey, quote.withStale(false));
                return quote.withStale(false);
            } catch (RuntimeException ex) {
                lastFailure = ex;
            }
        }

        ExchangeRateQuote cachedQuote = cachedRatesByPair.get(cacheKey);
        if (cachedQuote != null) {
            return cachedQuote.withStale(true);
        }

        throw new IllegalStateException("Unable to resolve exchange rate and no cached fallback available", lastFailure);
    }

    private ExchangeRateQuote executeWithTimeout(String sourceCurrency, String targetCurrency) {
        CompletableFuture<ExchangeRateQuote> future = CompletableFuture.supplyAsync(
                () -> delegate.resolveRate(sourceCurrency, targetCurrency));

        try {
            return future.get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Exchange rate lookup interrupted", e);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new IllegalStateException("Exchange rate lookup timed out", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Exchange rate lookup failed", cause);
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Exchange rate lookup failed", e);
        }
    }

    private String buildCacheKey(String sourceCurrency, String targetCurrency) {
        return sourceCurrency + "->" + targetCurrency;
    }
}
