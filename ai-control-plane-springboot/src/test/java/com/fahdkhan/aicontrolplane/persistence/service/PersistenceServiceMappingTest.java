package com.fahdkhan.aicontrolplane.persistence.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.model.StepStatus;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionStepDto;
import com.fahdkhan.aicontrolplane.persistence.dto.LlmMetadataDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepDependencyDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.entity.Instance;
import com.fahdkhan.aicontrolplane.persistence.entity.Plan;
import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionStep;
import com.fahdkhan.aicontrolplane.persistence.entity.LlmMetadata;
import com.fahdkhan.aicontrolplane.persistence.entity.StepDependency;
import com.fahdkhan.aicontrolplane.persistence.entity.StepExecution;
import com.fahdkhan.aicontrolplane.persistence.repository.InstanceRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionPlanRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionStepRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.LlmMetadataRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.StepDependencyRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.StepExecutionRepository;
import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PersistenceServiceMappingTest {

    @Test
    @Disabled
    void shouldMapDtoToEntityAndBackForEachService() {
        ExecutionPlanRepository executionPlanRepository = Mockito.mock(ExecutionPlanRepository.class);
        ExecutionStepRepository executionStepRepository = Mockito.mock(ExecutionStepRepository.class);
        StepDependencyRepository stepDependencyRepository = Mockito.mock(StepDependencyRepository.class);
        InstanceRepository instanceRepository = Mockito.mock(InstanceRepository.class);
        StepExecutionRepository stepExecutionRepository = Mockito.mock(StepExecutionRepository.class);
        LlmMetadataRepository llmMetadataRepository = Mockito.mock(LlmMetadataRepository.class);

        Plan plan = new Plan();
        plan.setPlanId("p1");
        plan.setMetadata("{}");
        plan.setCreatedAt(Instant.now());
        when(executionPlanRepository.save(any(Plan.class))).thenReturn(plan);
        when(executionPlanRepository.getReferenceById("p1")).thenReturn(plan);

        ExecutionStep step = new ExecutionStep();
        step.setStepId("s1");
        step.setPlan(plan);
        step.setToolName("tool");
        when(executionStepRepository.save(any(ExecutionStep.class))).thenReturn(step);
        when(executionStepRepository.getReferenceById("s1")).thenReturn(step);

        StepDependency dependency = new StepDependency();
        dependency.setId("sd1");
        when(stepDependencyRepository.save(any(StepDependency.class))).thenReturn(dependency);

        Instance instance = new Instance();
        instance.setInstanceId("e1");
        instance.setPlan(plan);
        instance.setStatus(ExecutionStatus.RUNNING);
        instance.setCreatedAt(Instant.now());
        when(instanceRepository.save(any(Instance.class))).thenReturn(instance);
        when(instanceRepository.getReferenceById("e1")).thenReturn(instance);

        StepExecution stepExecution = new StepExecution();
        stepExecution.setStepExecutionId("se1");
        stepExecution.setStatus(StepStatus.COMPLETED);
        when(stepExecutionRepository.save(any(StepExecution.class))).thenReturn(stepExecution);

        LlmMetadata llmMetadata = new LlmMetadata();
        llmMetadata.setInstanceId("e1");
        llmMetadata.setProviderId("openai");
        llmMetadata.setModelName("gpt");
        when(llmMetadataRepository.save(any(LlmMetadata.class))).thenReturn(llmMetadata);

        ExecutionPlanService executionPlanService = new ExecutionPlanService(executionPlanRepository);
        ExecutionStepService executionStepService = new ExecutionStepService(executionStepRepository, executionPlanRepository);
        StepDependencyService stepDependencyService = new StepDependencyService(stepDependencyRepository, executionStepRepository);
        ExecutionInstanceService executionInstanceService =
                new ExecutionInstanceService(instanceRepository, executionPlanRepository);
        StepExecutionService stepExecutionService =
                new StepExecutionService(stepExecutionRepository, instanceRepository, executionStepRepository);
        LlmMetadataService llmMetadataService = new LlmMetadataService(llmMetadataRepository, instanceRepository);

        assertEquals("p1", executionPlanService.save(new ExecutionPlanDto("p1", "{}", plan.getCreatedAt())).planId());
        assertEquals("s1", executionStepService.save(new ExecutionStepDto("s1", "p1", "tool", "{}", "{}")).stepId());
        //assertEquals("s0", stepDependencyService.save(new StepDependencyDto("s1", "s0")).stepId());
        assertEquals(
                "e1",
                executionInstanceService
                        .save(new ExecutionInstanceDto(
                                "e1", "p1", "user", "RUNNING", instance.getCreatedAt(), null, null, BigDecimal.ONE))
                        .instanceId());
        assertEquals(
                "e1",
                stepExecutionService
                        .save(new StepExecutionDto("e1", "i1", "s1", StepStatus.COMPLETED.toString(), "{}", null, null, null, 1L, BigDecimal.ONE))
                        .instanceId());
        assertEquals(
                "openai",
                llmMetadataService
                        .save(new LlmMetadataDto("e1", "openai", "gpt", 1, 1, BigDecimal.ONE, "raw"))
                        .providerId());
    }
}
