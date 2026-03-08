package com.fahdkhan.aicontrolplane.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class EntityBeanTest {

    @Test
    void shouldReadWriteEntityProperties() {
        ExecutionPlan plan = new ExecutionPlan();
        plan.setPlanId("p1");

        ExecutionStep step = new ExecutionStep();
        step.setId(new ExecutionStepId("p1", "s1"));
        step.setPlan(plan);

        ExecutionInstance instance = new ExecutionInstance();
        instance.setExecutionId("e1");
        instance.setPlan(plan);
        instance.setTotalCost(BigDecimal.ONE);
        instance.setCreatedAt(Instant.now());

        StepDependency dependency = new StepDependency();
        dependency.setId(new StepDependencyId("p1", "s1", "s0"));
        dependency.setStep(step);
        dependency.setDependsOnStep(step);

        StepExecution stepExecution = new StepExecution();
        stepExecution.setId(new StepExecutionId("e1", "s1"));
        stepExecution.setPlanId("p1");
        stepExecution.setExecution(instance);
        stepExecution.setStep(step);

        LlmMetadata metadata = new LlmMetadata();
        metadata.setExecutionId("e1");
        metadata.setExecution(instance);

        assertEquals("p1", step.getPlan().getPlanId());
        assertEquals("e1", stepExecution.getExecution().getExecutionId());
        assertEquals("s0", dependency.getId().getDependsOnStepId());
        assertEquals("e1", metadata.getExecution().getExecutionId());
    }
}
