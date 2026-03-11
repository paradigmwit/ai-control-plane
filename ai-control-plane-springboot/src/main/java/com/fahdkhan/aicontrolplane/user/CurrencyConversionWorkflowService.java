package com.fahdkhan.aicontrolplane.user;

import com.fahdkhan.aicontrolplane.api.user.CurrencyConversionWorkflowResponse;
import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.model.StepStatus;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionWorkflowService {

    public static final String PLAN_ID = "workflow.currency-conversion.v1";
    public static final String PARSE_STEP_ID = "currency-conversion.parse-prompt";
    public static final String RATE_STEP_ID = "currency-conversion.resolve-rate";
    public static final String CONVERT_STEP_ID = "currency-conversion.convert-amount";

    private static final Pattern PROMPT_PATTERN = Pattern.compile(
            "(?i).*?([0-9]+(?:\\.[0-9]+)?)\\s*([a-z]{3})\\s*(?:to|in|into)\\s*([a-z]{3}).*");

    private static final Map<String, BigDecimal> USD_VALUE_BY_CURRENCY = Map.of(
            "USD", BigDecimal.ONE,
            "EUR", new BigDecimal("1.08"),
            "GBP", new BigDecimal("1.27"),
            "JPY", new BigDecimal("0.0067"),
            "INR", new BigDecimal("0.012"),
            "PKR", new BigDecimal("0.0036"));

    private final ExecutionInstanceService executionInstanceService;
    private final StepExecutionService stepExecutionService;

    public CurrencyConversionWorkflowService(
            ExecutionInstanceService executionInstanceService,
            StepExecutionService stepExecutionService) {
        this.executionInstanceService = executionInstanceService;
        this.stepExecutionService = stepExecutionService;
    }

    public CurrencyConversionWorkflowResponse execute(String prompt) {
        ConversionInput input = parsePrompt(prompt);
        BigDecimal exchangeRate = getExchangeRate(input.sourceCurrency(), input.targetCurrency());
        BigDecimal convertedAmount = input.amount().multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);

        String instanceId = "instance-" + UUID.randomUUID();
        Instant now = Instant.now();

        executionInstanceService.save(new ExecutionInstanceDto(
                instanceId,
                PLAN_ID,
                ExecutionStatus.COMPLETED.name(),
                now,
                now,
                now,
                BigDecimal.ZERO));

        stepExecutionService.save(new StepExecutionDto(
                "step-exec-" + UUID.randomUUID(),
                instanceId,
                PARSE_STEP_ID,
                StepStatus.COMPLETED.name(),
                buildParseOutputJson(input),
                null,
                now,
                now,
                0L,
                BigDecimal.ZERO));

        stepExecutionService.save(new StepExecutionDto(
                "step-exec-" + UUID.randomUUID(),
                instanceId,
                RATE_STEP_ID,
                StepStatus.COMPLETED.name(),
                buildRateOutputJson(input.sourceCurrency(), input.targetCurrency(), exchangeRate),
                null,
                now,
                now,
                0L,
                BigDecimal.ZERO));

        String conversionOutput = buildConversionOutputJson(input, exchangeRate, convertedAmount);
        stepExecutionService.save(new StepExecutionDto(
                "step-exec-" + UUID.randomUUID(),
                instanceId,
                CONVERT_STEP_ID,
                StepStatus.COMPLETED.name(),
                conversionOutput,
                null,
                now,
                now,
                0L,
                BigDecimal.ZERO));

        return new CurrencyConversionWorkflowResponse(PLAN_ID, instanceId, conversionOutput);
    }

    private ConversionInput parsePrompt(String prompt) {
        Matcher matcher = PROMPT_PATTERN.matcher(prompt == null ? "" : prompt.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Prompt format must include amount and currencies, e.g. '100 USD to EUR'");
        }

        BigDecimal amount = new BigDecimal(matcher.group(1));
        String sourceCurrency = matcher.group(2).toUpperCase();
        String targetCurrency = matcher.group(3).toUpperCase();

        if (!USD_VALUE_BY_CURRENCY.containsKey(sourceCurrency) || !USD_VALUE_BY_CURRENCY.containsKey(targetCurrency)) {
            throw new IllegalArgumentException("Only USD, EUR, GBP, JPY, INR, and PKR are supported.");
        }

        return new ConversionInput(amount, sourceCurrency, targetCurrency);
    }

    private BigDecimal getExchangeRate(String sourceCurrency, String targetCurrency) {
        BigDecimal sourceInUsd = USD_VALUE_BY_CURRENCY.get(sourceCurrency);
        BigDecimal targetInUsd = USD_VALUE_BY_CURRENCY.get(targetCurrency);
        return sourceInUsd.divide(targetInUsd, 8, RoundingMode.HALF_UP);
    }

    private String buildParseOutputJson(ConversionInput input) {
        return String.format(
                "{\"amount\":%s,\"source_currency\":\"%s\",\"target_currency\":\"%s\"}",
                input.amount().toPlainString(),
                input.sourceCurrency(),
                input.targetCurrency());
    }

    private String buildRateOutputJson(String sourceCurrency, String targetCurrency, BigDecimal exchangeRate) {
        return String.format(
                "{\"source_currency\":\"%s\",\"target_currency\":\"%s\",\"exchange_rate\":%s}",
                sourceCurrency,
                targetCurrency,
                exchangeRate.toPlainString());
    }

    private String buildConversionOutputJson(ConversionInput input, BigDecimal exchangeRate, BigDecimal convertedAmount) {
        return String.format(
                "{\"source_amount\":%s,\"source_currency\":\"%s\",\"target_currency\":\"%s\",\"exchange_rate\":%s,\"converted_amount\":%s}",
                input.amount().toPlainString(),
                input.sourceCurrency(),
                input.targetCurrency(),
                exchangeRate.toPlainString(),
                convertedAmount.toPlainString());
    }

    private record ConversionInput(BigDecimal amount, String sourceCurrency, String targetCurrency) {
    }
}
