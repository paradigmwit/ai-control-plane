package com.fahdkhan.aicontrolplane.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.api.Test;

class CurrencyConversionWorkflowServiceTest {

    @Test
    void shouldExecuteCurrencyConversionWorkflow() {
        ExecutionInstanceService executionInstanceService = mock(ExecutionInstanceService.class);
        StepExecutionService stepExecutionService = mock(StepExecutionService.class);

        when(executionInstanceService.save(any())).thenAnswer(i -> i.getArgument(0, ExecutionInstanceDto.class));
        when(stepExecutionService.save(any())).thenAnswer(i -> i.getArgument(0, StepExecutionDto.class));

        CurrencyConversionWorkflowService service = new CurrencyConversionWorkflowService(
                executionInstanceService, stepExecutionService);

        CurrencyConversionWorkflowResponse response = service.execute("Convert 100 USD to EUR");

        assertEquals(CurrencyConversionWorkflowService.PLAN_ID, response.planId());
        verify(executionInstanceService, times(1)).save(any());
        verify(stepExecutionService, times(3)).save(any());
    }

    @Test
    void shouldFailForInvalidPrompt() {
        CurrencyConversionWorkflowService service = new CurrencyConversionWorkflowService(
                mock(ExecutionInstanceService.class), mock(StepExecutionService.class));

        assertThrows(IllegalArgumentException.class, () -> service.execute("convert money please"));
    }
}
