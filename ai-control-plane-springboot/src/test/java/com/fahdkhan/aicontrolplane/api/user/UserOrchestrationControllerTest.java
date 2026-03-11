package com.fahdkhan.aicontrolplane.api.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class UserOrchestrationControllerTest {

    @Test
    void shouldCreateExecutionPlan() {
        ExecutionPlanService planService = mock(ExecutionPlanService.class);
        when(planService.save(any())).thenReturn(new ExecutionPlanDto("p1", "{}", Instant.now()));

        UserOrchestrationController controller = new UserOrchestrationController(
                planService, mock(ExecutionInstanceService.class), mock(StepExecutionService.class));

        var response = controller.createPlan(
                new UserOrchestrationRequest("task", "meta"),
                new UsernamePasswordAuthenticationToken("user", "n/a"));

        assertEquals("p1", response.planId());
    }

    @Test
    void shouldStartExecution() {
        ExecutionInstanceService instanceService = mock(ExecutionInstanceService.class);
        when(instanceService.save(any()))
                .thenReturn(new ExecutionInstanceDto("i1", "p1", "RUNNING", Instant.now(), Instant.now(), null, BigDecimal.ZERO));

        UserOrchestrationController controller = new UserOrchestrationController(
                mock(ExecutionPlanService.class), instanceService, mock(StepExecutionService.class));

        assertEquals("i1", controller.executePlan("p1").instanceId());
    }

    @Test
    void shouldMarkStepCompleted() {
        StepExecutionService stepExecutionService = mock(StepExecutionService.class);
        when(stepExecutionService.save(any()))
                .thenReturn(new StepExecutionDto("se1", "i1", "s1", "COMPLETED", "{}", null, Instant.now(), Instant.now(), 0L, BigDecimal.ZERO));

        UserOrchestrationController controller = new UserOrchestrationController(
                mock(ExecutionPlanService.class), mock(ExecutionInstanceService.class), stepExecutionService);

        assertEquals("COMPLETED", controller.markStepCompleted("i1", "s1").status());
    }
}
