package com.fahdkhan.aicontrolplane.persistence.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fahdkhan.aicontrolplane.model.StepStatus;
import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionStep;
import com.fahdkhan.aicontrolplane.persistence.entity.Instance;
import com.fahdkhan.aicontrolplane.persistence.entity.StepExecution;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionStepRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.InstanceRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.StepExecutionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class StepExecutionServiceTest {

    @Test
    void shouldCompleteExistingRunningStepExecution() {
        StepExecutionRepository repository = mock(StepExecutionRepository.class);
        when(repository.findByInstance_InstanceIdAndStep_StepId("i1", "s1")).thenReturn(Optional.of(stepExecution(StepStatus.RUNNING)));
        when(repository.save(any(StepExecution.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepExecutionService service = new StepExecutionService(
                repository,
                mock(InstanceRepository.class),
                mock(ExecutionStepRepository.class));

        var result = service.completeStepExecution("i1", "s1");

        assertTrue(result.isPresent());
        assertEquals("COMPLETED", result.get().status());
        assertTrue(result.get().completedAt() != null);
        assertTrue(result.get().executionTimeMs() >= 0L);
        assertEquals(BigDecimal.ZERO, result.get().stepCost());
    }

    @Test
    void shouldRejectInvalidStepStatusTransition() {
        StepExecutionRepository repository = mock(StepExecutionRepository.class);
        when(repository.findByInstance_InstanceIdAndStep_StepId("i1", "s1")).thenReturn(Optional.of(stepExecution(StepStatus.COMPLETED)));

        StepExecutionService service = new StepExecutionService(
                repository,
                mock(InstanceRepository.class),
                mock(ExecutionStepRepository.class));

        assertThrows(IllegalStateException.class, () -> service.completeStepExecution("i1", "s1"));
    }

    @Test
    void shouldReturnEmptyWhenStepExecutionMissing() {
        StepExecutionRepository repository = mock(StepExecutionRepository.class);
        when(repository.findByInstance_InstanceIdAndStep_StepId("i1", "missing")).thenReturn(Optional.empty());

        StepExecutionService service = new StepExecutionService(
                repository,
                mock(InstanceRepository.class),
                mock(ExecutionStepRepository.class));

        assertTrue(service.completeStepExecution("i1", "missing").isEmpty());
    }

    private StepExecution stepExecution(StepStatus status) {
        Instance instance = new Instance();
        instance.setInstanceId("i1");

        ExecutionStep step = new ExecutionStep();
        step.setStepId("s1");

        StepExecution stepExecution = new StepExecution();
        stepExecution.setStepExecutionId("se1");
        stepExecution.setInstance(instance);
        stepExecution.setStep(step);
        stepExecution.setStatus(status);
        stepExecution.setStartedAt(Instant.now().minusSeconds(5));
        return stepExecution;
    }
}
