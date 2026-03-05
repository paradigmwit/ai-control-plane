package com.fahdkhan.aicontrolplane.persistence.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PersistenceDtoTest {

    @Test
    void shouldExposeRecordFields() {
        Instant now = Instant.now();
        ExecutionPlanDto executionPlanDto = new ExecutionPlanDto("p1", "{}", now);
        ExecutionStepDto executionStepDto = new ExecutionStepDto("s1", "p1", "tool", "{}", "{}");
        StepDependencyDto stepDependencyDto = new StepDependencyDto("s1", "s0");
        ExecutionInstanceDto executionInstanceDto =
                new ExecutionInstanceDto("e1", "p1", "RUNNING", now, now, now, BigDecimal.ONE);
        StepExecutionDto stepExecutionDto =
                new StepExecutionDto("e1", "s1", "DONE", "{}", null, now, now, 42L, BigDecimal.TEN);
        LlmMetadataDto llmMetadataDto =
                new LlmMetadataDto("e1", "openai", "gpt", 1, 2, BigDecimal.ONE, "raw");

        assertEquals("p1", executionPlanDto.planId());
        assertEquals("tool", executionStepDto.toolName());
        assertEquals("s0", stepDependencyDto.dependsOnStepId());
        assertEquals("RUNNING", executionInstanceDto.status());
        assertEquals(42L, stepExecutionDto.executionTimeMs());
        assertEquals("openai", llmMetadataDto.providerId());
    }
}
