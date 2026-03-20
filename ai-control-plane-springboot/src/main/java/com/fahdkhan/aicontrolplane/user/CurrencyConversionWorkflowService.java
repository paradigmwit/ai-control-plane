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

    private final ExecutionInstanceService executionInstanceService;
    private final StepExecutionService stepExecutionService;
    private final ExchangeRateProvider exchangeRateProvider;

    public CurrencyConversionWorkflowService(
            ExecutionInstanceService executionInstanceService,
            StepExecutionService stepExecutionService,
            ExchangeRateProvider exchangeRateProvider) {
        this.executionInstanceService = executionInstanceService;
        this.stepExecutionService = stepExecutionService;
        this.exchangeRateProvider = exchangeRateProvider;
    }

    public CurrencyConversionWorkflowResponse execute(String prompt) {
        ConversionInput input = parsePrompt(prompt);
        ExchangeRateQuote exchangeRateQuote = exchangeRateProvider.resolveRate(input.sourceCurrency(), input.targetCurrency());
        BigDecimal convertedAmount = input.amount().multiply(exchangeRateQuote.exchangeRate()).setScale(4, RoundingMode.HALF_UP);

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
                buildRateOutputJson(input, exchangeRateQuote),
                null,
                now,
                now,
                0L,
                BigDecimal.ZERO));

        String conversionOutput = buildConversionOutputJson(input, exchangeRateQuote, convertedAmount);
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

        return new ConversionInput(amount, sourceCurrency, targetCurrency);
    }

    private String buildParseOutputJson(ConversionInput input) {
        return String.format(
                "{\"amount\":%s,\"source_currency\":\"%s\",\"target_currency\":\"%s\"}",
                input.amount().toPlainString(),
                input.sourceCurrency(),
                input.targetCurrency());
    }

    private String buildRateOutputJson(ConversionInput input, ExchangeRateQuote quote) {
        return String.format(
                "{\"source_currency\":\"%s\",\"target_currency\":\"%s\",\"exchange_rate\":%s,\"rate_source\":\"%s\",\"rate_timestamp\":\"%s\",\"rate_confidence\":%s,\"rate_version\":\"%s\",\"rate_stale\":%s}",
                input.sourceCurrency(),
                input.targetCurrency(),
                quote.exchangeRate().toPlainString(),
                quote.rateSource(),
                quote.rateTimestamp(),
                quote.confidence().toPlainString(),
                quote.version(),
                quote.stale());
    }

    private String buildConversionOutputJson(
            ConversionInput input, ExchangeRateQuote quote, BigDecimal convertedAmount) {
        return String.format(
                "{\"source_amount\":%s,\"source_currency\":\"%s\",\"target_currency\":\"%s\",\"exchange_rate\":%s,\"converted_amount\":%s,\"rate_source\":\"%s\",\"rate_timestamp\":\"%s\",\"rate_stale\":%s}",
                input.amount().toPlainString(),
                input.sourceCurrency(),
                input.targetCurrency(),
                quote.exchangeRate().toPlainString(),
                convertedAmount.toPlainString(),
                quote.rateSource(),
                quote.rateTimestamp(),
                quote.stale());
    }

    private record ConversionInput(BigDecimal amount, String sourceCurrency, String targetCurrency) {
    }
}
