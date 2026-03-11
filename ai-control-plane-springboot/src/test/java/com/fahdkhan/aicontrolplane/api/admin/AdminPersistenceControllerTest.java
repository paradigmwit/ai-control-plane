package com.fahdkhan.aicontrolplane.api.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionStepService;
import com.fahdkhan.aicontrolplane.persistence.service.LlmMetadataService;
import com.fahdkhan.aicontrolplane.persistence.service.StepDependencyService;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdminPersistenceControllerTest {

    @Test
    void shouldReturnPlanList() {
        ExecutionPlanService planService = mock(ExecutionPlanService.class);
        when(planService.findAll()).thenReturn(List.of(new ExecutionPlanDto("p1", "{}", Instant.now())));

        AdminPersistenceController controller = new AdminPersistenceController(
                planService,
                mock(ExecutionStepService.class),
                mock(StepDependencyService.class),
                mock(ExecutionInstanceService.class),
                mock(StepExecutionService.class),
                mock(LlmMetadataService.class));

        assertEquals(1, controller.listPlans().size());
    }

    @Test
    void shouldReturnNotFoundWhenPlanMissing() {
        ExecutionPlanService planService = mock(ExecutionPlanService.class);
        when(planService.findById("missing")).thenReturn(java.util.Optional.empty());

        AdminPersistenceController controller = new AdminPersistenceController(
                planService,
                mock(ExecutionStepService.class),
                mock(StepDependencyService.class),
                mock(ExecutionInstanceService.class),
                mock(StepExecutionService.class),
                mock(LlmMetadataService.class));

        assertTrue(controller.getPlan("missing").getStatusCode().is4xxClientError());
    }
}
