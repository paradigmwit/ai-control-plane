package com.fahdkhan.aicontrolplane.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.model.StepStatus;
import com.fahdkhan.aicontrolplane.persistence.entity.Instance;
import com.fahdkhan.aicontrolplane.persistence.entity.StepExecution;
import com.fahdkhan.aicontrolplane.persistence.repository.InstanceRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.StepExecutionRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CurrencyConversionWorkflowPersistenceIntegrationTest {

    @Autowired
    private CurrencyConversionWorkflowService workflowService;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private StepExecutionRepository stepExecutionRepository;

    @BeforeEach
    void setUp() {
        stepExecutionRepository.deleteAll();
        instanceRepository.deleteAll();
    }

    @Test
    void shouldPersistFailureStateWhenRateLookupFails() {
        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> workflowService.execute("Convert 100 USD to XYZ"));

        assertEquals("Unable to resolve exchange rate and no cached fallback available", error.getMessage());

        List<Instance> instances = instanceRepository.findAll();
        assertEquals(1, instances.size());
        Instance instance = instances.get(0);
        assertEquals(ExecutionStatus.FAILED, instance.getStatus());
        assertNotNull(instance.getCompletedAt());
        assertTrue(instance.getErrorPayload().contains("Unable to resolve exchange rate"));

        List<StepExecution> stepExecutions = stepExecutionRepository.findByInstanceInstanceId(instance.getInstanceId());
        assertEquals(1, stepExecutions.size());
        StepExecution failedStep = stepExecutions.get(0);
        assertEquals(CurrencyConversionWorkflowService.RATE_STEP_ID, failedStep.getStep().getStepId());
        assertEquals(StepStatus.FAILED, failedStep.getStatus());
        assertNotNull(failedStep.getCompletedAt());
        assertTrue(failedStep.getErrorMessage().contains("Unable to resolve exchange rate"));
    }
}
