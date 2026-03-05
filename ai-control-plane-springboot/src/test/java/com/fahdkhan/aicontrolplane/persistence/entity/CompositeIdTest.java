package com.fahdkhan.aicontrolplane.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CompositeIdTest {

    @Test
    void shouldSupportValueEquality() {
        StepDependencyId dependencyIdA = new StepDependencyId("s1", "s2");
        StepDependencyId dependencyIdB = new StepDependencyId("s1", "s2");
        StepExecutionId executionIdA = new StepExecutionId("e1", "s1");
        StepExecutionId executionIdB = new StepExecutionId("e1", "s1");

        assertEquals(dependencyIdA, dependencyIdB);
        assertEquals(dependencyIdA.hashCode(), dependencyIdB.hashCode());
        assertEquals(executionIdA, executionIdB);
        assertEquals(executionIdA.hashCode(), executionIdB.hashCode());
    }
}
