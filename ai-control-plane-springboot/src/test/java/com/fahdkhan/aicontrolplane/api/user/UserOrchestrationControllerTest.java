package com.fahdkhan.aicontrolplane.api.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
import com.fahdkhan.aicontrolplane.user.CurrencyConversionWorkflowService;
import com.fahdkhan.aicontrolplane.api.user.CurrencyConversionWorkflowResponse;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class UserOrchestrationControllerTest {

    @Test
    void shouldCreateExecutionPlan() {
        ExecutionPlanService planService = mock(ExecutionPlanService.class);
        when(planService.save(any())).thenReturn(new ExecutionPlanDto("p1", "{}", Instant.now()));

        UserOrchestrationController controller = new UserOrchestrationController(
                planService,
                mock(ExecutionInstanceService.class),
                mock(StepExecutionService.class),
                mock(CurrencyConversionWorkflowService.class));

        var response = controller.createPlan(
                new UserOrchestrationRequest("task", "meta"),
                new UsernamePasswordAuthenticationToken("user", "n/a"));

        assertEquals("p1", response.planId());
    }

    @Test
    void shouldStartExecution() {
        ExecutionInstanceService instanceService = mock(ExecutionInstanceService.class);
        when(instanceService.save(any()))
                .thenReturn(new ExecutionInstanceDto("i1", "p1", "RUNNING", Instant.now(), Instant.now(), null, BigDecimal.ZERO, null));

        UserOrchestrationController controller = new UserOrchestrationController(
                mock(ExecutionPlanService.class),
                instanceService,
                mock(StepExecutionService.class),
                mock(CurrencyConversionWorkflowService.class));

        assertEquals("i1", controller.executePlan("p1").instanceId());
    }

    @Test
    void shouldMarkStepCompleted() {
        StepExecutionService stepExecutionService = mock(StepExecutionService.class);
        when(stepExecutionService.completeStepExecution("i1", "s1"))
                .thenReturn(java.util.Optional.of(new StepExecutionDto("se1", "i1", "s1", "COMPLETED", "{}", null, Instant.now(), Instant.now(), 0L, BigDecimal.ZERO)));

        UserOrchestrationController controller = new UserOrchestrationController(
                mock(ExecutionPlanService.class),
                mock(ExecutionInstanceService.class),
                stepExecutionService,
                mock(CurrencyConversionWorkflowService.class));

        var response = controller.markStepCompleted("i1", "s1");
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("COMPLETED", response.getBody().status());
    }


    @Test
    void shouldReturnConflictForInvalidStepTransition() {
        StepExecutionService stepExecutionService = mock(StepExecutionService.class);
        when(stepExecutionService.completeStepExecution("i1", "s1"))
                .thenThrow(new IllegalStateException("invalid transition"));

        UserOrchestrationController controller = new UserOrchestrationController(
                mock(ExecutionPlanService.class),
                mock(ExecutionInstanceService.class),
                stepExecutionService,
                mock(CurrencyConversionWorkflowService.class));

        var response = controller.markStepCompleted("i1", "s1");

        assertEquals(HttpStatusCode.valueOf(409), response.getStatusCode());
        assertTrue(response.getBody() == null);
    }

    @Test
    void shouldReturnNotFoundForMissingStepExecution() {
        StepExecutionService stepExecutionService = mock(StepExecutionService.class);
        when(stepExecutionService.completeStepExecution("i1", "missing"))
                .thenReturn(java.util.Optional.empty());

        UserOrchestrationController controller = new UserOrchestrationController(
                mock(ExecutionPlanService.class),
                mock(ExecutionInstanceService.class),
                stepExecutionService,
                mock(CurrencyConversionWorkflowService.class));

        var response = controller.markStepCompleted("i1", "missing");

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    void shouldExecuteCurrencyConversionWorkflow() {
        CurrencyConversionWorkflowService workflowService = mock(CurrencyConversionWorkflowService.class);
        when(workflowService.execute("convert 50 usd to eur"))
                .thenReturn(new CurrencyConversionWorkflowResponse("p1", "i1", "{}"));

        UserOrchestrationController controller = new UserOrchestrationController(
                mock(ExecutionPlanService.class),
                mock(ExecutionInstanceService.class),
                mock(StepExecutionService.class),
                workflowService);

        var response = controller.executeCurrencyConversionWorkflow(new CurrencyConversionWorkflowRequest("convert 50 usd to eur"));

        assertEquals("i1", response.getBody().instanceId());
    }

    @Test
    void shouldReturnBadRequestForInvalidCurrencyPrompt() {
        CurrencyConversionWorkflowService workflowService = mock(CurrencyConversionWorkflowService.class);
        when(workflowService.execute("bad prompt")).thenThrow(new IllegalArgumentException("bad"));

        UserOrchestrationController controller = new UserOrchestrationController(
                mock(ExecutionPlanService.class),
                mock(ExecutionInstanceService.class),
                mock(StepExecutionService.class),
                workflowService);

        var response = controller.executeCurrencyConversionWorkflow(new CurrencyConversionWorkflowRequest("bad prompt"));

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
}
