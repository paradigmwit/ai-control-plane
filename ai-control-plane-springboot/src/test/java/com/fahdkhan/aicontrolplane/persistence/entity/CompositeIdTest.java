package com.fahdkhan.aicontrolplane.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CompositeIdTest {

    @Test
    void shouldSupportValueEquality() {
        ExecutionStepId stepIdA = new ExecutionStepId("p1", "s1");
        ExecutionStepId stepIdB = new ExecutionStepId("p1", "s1");
        StepDependencyId dependencyIdA = new StepDependencyId("p1", "s1", "s2");
        StepDependencyId dependencyIdB = new StepDependencyId("p1", "s1", "s2");
        StepExecutionId executionIdA = new StepExecutionId("e1", "s1");
        StepExecutionId executionIdB = new StepExecutionId("e1", "s1");

        assertEquals(stepIdA, stepIdB);
        assertEquals(stepIdA.hashCode(), stepIdB.hashCode());
        assertEquals(dependencyIdA, dependencyIdB);
        assertEquals(dependencyIdA.hashCode(), dependencyIdB.hashCode());
        assertEquals(executionIdA, executionIdB);
        assertEquals(executionIdA.hashCode(), executionIdB.hashCode());
    }
}
