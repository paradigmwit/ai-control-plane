package com.fahdkhan.aicontrolplane.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class EntityBeanTest {

    @Test
    void shouldReadWriteEntityProperties() {
        Plan plan = new Plan();
        plan.setPlanId("p1");

        ExecutionStep step = new ExecutionStep();
        step.setStepId("s1");
        step.setPlan(plan);

        ExecutionStep step2 = new ExecutionStep();
        step2.setStepId("s2");
        step2.setPlan(plan);


        Instance instance = new Instance();
        instance.setInstanceId("e1");
        instance.setPlan(plan);
        instance.setTotalCost(BigDecimal.ONE);
        instance.setCreatedAt(Instant.now());

        StepDependency dependency = new StepDependency();
        dependency.setId("sd1");
        dependency.setPlan(plan);
        dependency.setStep(step);
        dependency.setDependsOnStep(null);

        StepDependency dependency2 = new StepDependency();
        dependency.setPlan(plan);
        dependency.setId("sd2");
        dependency.setStep(step2);
        dependency.setDependsOnStep(step);

        StepExecution stepExecution = new StepExecution();
        stepExecution.setStepExecutionId("s1");
        stepExecution.setInstance(instance);
        stepExecution.setStep(step);

        LlmMetadata metadata = new LlmMetadata();
        metadata.setInstanceId("e1");
        metadata.setInstance(instance);

        assertEquals("p1", step.getPlan().getPlanId());
        assertEquals("e1", stepExecution.getInstance().getInstanceId());
        assertEquals("s1", dependency.getDependsOnStep().getStepId());
        assertEquals("e1", metadata.getInstanceId());
    }
}
