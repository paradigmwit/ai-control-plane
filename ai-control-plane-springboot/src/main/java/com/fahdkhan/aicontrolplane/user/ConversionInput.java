package com.fahdkhan.aicontrolplane.user;

import java.math.BigDecimal;

record ConversionInput(BigDecimal amount, String sourceCurrency, String targetCurrency) {
}
