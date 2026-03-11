package com.fahdkhan.aicontrolplane.persistence.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record StepExecutionDto(
        String stepExecutionId,
        String instanceId,
        String stepId,
        String status,
        String outputPayload,
        String errorMessage,
        Instant startedAt,
        Instant completedAt,
        Long executionTimeMs,
        BigDecimal stepCost) {
}
