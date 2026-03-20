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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    private final CurrencyConversionWorkflowFailurePersistenceService failurePersistenceService;

    public CurrencyConversionWorkflowService(
            ExecutionInstanceService executionInstanceService,
            StepExecutionService stepExecutionService,
            ExchangeRateProvider exchangeRateProvider,
            CurrencyConversionWorkflowFailurePersistenceService failurePersistenceService) {
        this.executionInstanceService = executionInstanceService;
        this.stepExecutionService = stepExecutionService;
        this.exchangeRateProvider = exchangeRateProvider;
        this.failurePersistenceService = failurePersistenceService;
    }

    @Transactional
    public CurrencyConversionWorkflowResponse execute(String prompt) {
        String instanceId = "instance-" + UUID.randomUUID();
        Instant executionStartedAt = Instant.now();
        String currentStepExecutionId = null;
        String currentStepId = null;
        Instant currentStepStartedAt = null;

        executionInstanceService.save(new ExecutionInstanceDto(
                instanceId,
                PLAN_ID,
                ExecutionStatus.RUNNING.name(),
                executionStartedAt,
                executionStartedAt,
                null,
                BigDecimal.ZERO,
                null));

        try {
            currentStepId = PARSE_STEP_ID;
            currentStepExecutionId = "step-exec-" + UUID.randomUUID();
            currentStepStartedAt = Instant.now();
            saveRunningStep(instanceId, currentStepExecutionId, currentStepId, currentStepStartedAt);
            ConversionInput input = parsePrompt(prompt);
            completeStep(instanceId, currentStepExecutionId, currentStepId, currentStepStartedAt, buildParseOutputJson(input));

            currentStepId = RATE_STEP_ID;
            currentStepExecutionId = "step-exec-" + UUID.randomUUID();
            currentStepStartedAt = Instant.now();
            saveRunningStep(instanceId, currentStepExecutionId, currentStepId, currentStepStartedAt);
            ExchangeRateQuote exchangeRateQuote = exchangeRateProvider.resolveRate(input.sourceCurrency(), input.targetCurrency());
            completeStep(instanceId, currentStepExecutionId, currentStepId, currentStepStartedAt, buildRateOutputJson(input, exchangeRateQuote));

            currentStepId = CONVERT_STEP_ID;
            currentStepExecutionId = "step-exec-" + UUID.randomUUID();
            currentStepStartedAt = Instant.now();
            saveRunningStep(instanceId, currentStepExecutionId, currentStepId, currentStepStartedAt);
            BigDecimal convertedAmount = input.amount().multiply(exchangeRateQuote.exchangeRate()).setScale(4, RoundingMode.HALF_UP);
            String conversionOutput = buildConversionOutputJson(input, exchangeRateQuote, convertedAmount);
            completeStep(instanceId, currentStepExecutionId, currentStepId, currentStepStartedAt, conversionOutput);

            Instant completedAt = Instant.now();
            executionInstanceService.save(new ExecutionInstanceDto(
                    instanceId,
                    PLAN_ID,
                    ExecutionStatus.COMPLETED.name(),
                    executionStartedAt,
                    executionStartedAt,
                    completedAt,
                    BigDecimal.ZERO,
                    null));

            return new CurrencyConversionWorkflowResponse(PLAN_ID, instanceId, conversionOutput);
        } catch (RuntimeException ex) {
            Instant failedAt = Instant.now();
            markTransactionForRollback();
            failurePersistenceService.persistFailure(
                    instanceId,
                    currentStepExecutionId,
                    currentStepId,
                    executionStartedAt,
                    currentStepStartedAt != null ? currentStepStartedAt : executionStartedAt,
                    failedAt,
                    buildErrorPayload(ex));
            throw ex;
        }
    }

    private void saveRunningStep(String instanceId, String stepExecutionId, String stepId, Instant startedAt) {
        stepExecutionService.save(new StepExecutionDto(
                stepExecutionId,
                instanceId,
                stepId,
                StepStatus.RUNNING.name(),
                null,
                null,
                startedAt,
                null,
                0L,
                BigDecimal.ZERO));
    }

    private void completeStep(
            String instanceId,
            String stepExecutionId,
            String stepId,
            Instant startedAt,
            String outputPayload) {
        Instant completedAt = Instant.now();
        stepExecutionService.save(new StepExecutionDto(
                stepExecutionId,
                instanceId,
                stepId,
                StepStatus.COMPLETED.name(),
                outputPayload,
                null,
                startedAt,
                completedAt,
                Math.max(0L, completedAt.toEpochMilli() - startedAt.toEpochMilli()),
                BigDecimal.ZERO));
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


    private void markTransactionForRollback() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    private String buildErrorPayload(RuntimeException ex) {
        return String.format(
                "{\"error_type\":\"%s\",\"message\":\"%s\"}",
                ex.getClass().getSimpleName(),
                escapeJson(ex.getMessage()));
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private record ConversionInput(BigDecimal amount, String sourceCurrency, String targetCurrency) {
    }
}
