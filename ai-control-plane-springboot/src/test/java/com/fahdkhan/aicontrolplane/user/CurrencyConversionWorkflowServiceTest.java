package com.fahdkhan.aicontrolplane.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fahdkhan.aicontrolplane.api.user.CurrencyConversionWorkflowResponse;
import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.model.StepStatus;
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
        CurrencyConversionWorkflowFailurePersistenceService failurePersistenceService =
                mock(CurrencyConversionWorkflowFailurePersistenceService.class);

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
                executionInstanceService,
                stepExecutionService,
                exchangeRateProvider,
                failurePersistenceService);

        CurrencyConversionWorkflowResponse response = service.execute("Convert 100 USD to EUR");

        assertEquals(CurrencyConversionWorkflowService.PLAN_ID, response.planId());
        assertTrue(response.outputPayload().contains("\"rate_source\":\"internal-feed\""));
        assertTrue(response.outputPayload().contains("\"rate_timestamp\":\"2026-01-01T00:00:00Z\""));

        ArgumentCaptor<ExecutionInstanceDto> instanceCaptor = ArgumentCaptor.forClass(ExecutionInstanceDto.class);
        verify(executionInstanceService, times(2)).save(instanceCaptor.capture());
        List<ExecutionInstanceDto> savedInstances = instanceCaptor.getAllValues();
        assertEquals(ExecutionStatus.RUNNING.name(), savedInstances.get(0).status());
        assertEquals(ExecutionStatus.COMPLETED.name(), savedInstances.get(1).status());

        ArgumentCaptor<StepExecutionDto> stepCaptor = ArgumentCaptor.forClass(StepExecutionDto.class);
        verify(stepExecutionService, times(6)).save(stepCaptor.capture());

        List<StepExecutionDto> savedSteps = stepCaptor.getAllValues();
        assertEquals(StepStatus.RUNNING.name(), savedSteps.get(0).status());
        assertEquals(StepStatus.COMPLETED.name(), savedSteps.get(1).status());
        assertEquals(CurrencyConversionWorkflowService.PARSE_STEP_ID, savedSteps.get(0).stepId());
        assertEquals(CurrencyConversionWorkflowService.PARSE_STEP_ID, savedSteps.get(1).stepId());
        assertEquals(CurrencyConversionWorkflowService.RATE_STEP_ID, savedSteps.get(2).stepId());
        assertEquals(CurrencyConversionWorkflowService.RATE_STEP_ID, savedSteps.get(3).stepId());
        assertEquals(CurrencyConversionWorkflowService.CONVERT_STEP_ID, savedSteps.get(4).stepId());
        assertEquals(CurrencyConversionWorkflowService.CONVERT_STEP_ID, savedSteps.get(5).stepId());
        assertTrue(savedSteps.get(3).outputPayload().contains("\"rate_source\":\"internal-feed\""));

        verify(failurePersistenceService, never()).persistFailure(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldPersistFailureContextWhenRateLookupFails() {
        ExecutionInstanceService executionInstanceService = mock(ExecutionInstanceService.class);
        StepExecutionService stepExecutionService = mock(StepExecutionService.class);
        ExchangeRateProvider exchangeRateProvider = mock(ExchangeRateProvider.class);
        CurrencyConversionWorkflowFailurePersistenceService failurePersistenceService =
                mock(CurrencyConversionWorkflowFailurePersistenceService.class);

        when(executionInstanceService.save(any())).thenAnswer(i -> i.getArgument(0, ExecutionInstanceDto.class));
        when(stepExecutionService.save(any())).thenAnswer(i -> i.getArgument(0, StepExecutionDto.class));
        when(exchangeRateProvider.resolveRate("USD", "EUR")).thenThrow(new IllegalStateException("lookup failed"));

        CurrencyConversionWorkflowService service = new CurrencyConversionWorkflowService(
                executionInstanceService,
                stepExecutionService,
                exchangeRateProvider,
                failurePersistenceService);

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> service.execute("Convert 100 USD to EUR"));

        assertEquals("lookup failed", error.getMessage());
        verify(executionInstanceService, times(1)).save(any());
        verify(stepExecutionService, times(3)).save(any());
        verify(failurePersistenceService).persistFailure(
                any(),
                any(),
                eq(CurrencyConversionWorkflowService.RATE_STEP_ID),
                any(),
                any(),
                any(),
                eq("{\"error_type\":\"IllegalStateException\",\"message\":\"lookup failed\"}"));
    }

    @Test
    void shouldFailForInvalidPrompt() {
        CurrencyConversionWorkflowService service = new CurrencyConversionWorkflowService(
                mock(ExecutionInstanceService.class),
                mock(StepExecutionService.class),
                mock(ExchangeRateProvider.class),
                mock(CurrencyConversionWorkflowFailurePersistenceService.class));

        assertThrows(IllegalArgumentException.class, () -> service.execute("convert money please"));
    }
}
