package com.fahdkhan.aicontrolplane.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fahdkhan.aicontrolplane.api.user.CurrencyConversionWorkflowResponse;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CurrencyConversionWorkflowServiceTest {

    @Test
    void shouldExecuteCurrencyConversionWorkflow() {
        ExecutionInstanceService executionInstanceService = mock(ExecutionInstanceService.class);
        StepExecutionService stepExecutionService = mock(StepExecutionService.class);
        ExchangeRateProvider exchangeRateProvider = mock(ExchangeRateProvider.class);

        when(executionInstanceService.save(any())).thenAnswer(i -> i.getArgument(0, ExecutionInstanceDto.class));
        when(stepExecutionService.save(any())).thenAnswer(i -> i.getArgument(0, StepExecutionDto.class));
        when(exchangeRateProvider.resolveRate("USD", "EUR")).thenReturn(new ExchangeRateQuote(
                new BigDecimal("0.92592593"),
                "internal-feed",
                Instant.parse("2026-01-01T00:00:00Z"),
                new BigDecimal("0.99"),
                "internal-feed-v2",
                false));

        CurrencyConversionWorkflowService service = new CurrencyConversionWorkflowService(
                executionInstanceService, stepExecutionService, exchangeRateProvider);

        CurrencyConversionWorkflowResponse response = service.execute("Convert 100 USD to EUR");

        assertEquals(CurrencyConversionWorkflowService.PLAN_ID, response.planId());
        assertTrue(response.outputPayload().contains("\"rate_source\":\"internal-feed\""));
        assertTrue(response.outputPayload().contains("\"rate_timestamp\":\"2026-01-01T00:00:00Z\""));

        verify(executionInstanceService, times(1)).save(any());
        verify(stepExecutionService, times(3)).save(any());

        ArgumentCaptor<StepExecutionDto> stepCaptor = ArgumentCaptor.forClass(StepExecutionDto.class);
        verify(stepExecutionService, times(3)).save(stepCaptor.capture());

        List<StepExecutionDto> savedSteps = stepCaptor.getAllValues();
        StepExecutionDto rateStep = savedSteps.stream()
                .filter(dto -> CurrencyConversionWorkflowService.RATE_STEP_ID.equals(dto.stepId()))
                .findFirst()
                .orElseThrow();
        assertTrue(rateStep.outputPayload().contains("\"rate_source\":\"internal-feed\""));
        assertTrue(rateStep.outputPayload().contains("\"rate_timestamp\":\"2026-01-01T00:00:00Z\""));
    }

    @Test
    void shouldFailForInvalidPrompt() {
        CurrencyConversionWorkflowService service = new CurrencyConversionWorkflowService(
                mock(ExecutionInstanceService.class),
                mock(StepExecutionService.class),
                mock(ExchangeRateProvider.class));

        assertThrows(IllegalArgumentException.class, () -> service.execute("convert money please"));
    }
}
