package com.fahdkhan.aicontrolplane.user;

public interface ExchangeRateProvider {

    ExchangeRateQuote resolveRate(String sourceCurrency, String targetCurrency);
}
