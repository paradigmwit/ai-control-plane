package com.fahdkhan.aicontrolplane.persistence.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;

public record StepExecutionDto(
        @NotBlank String executionId,
        @NotBlank String planId,
        @NotBlank String stepId,
        @NotBlank @Pattern(regexp = "PENDING|READY|RUNNING|COMPLETED|FAILED|SKIPPED") String status,
        String outputPayload,
        String errorMessage,
        Instant startedAt,
        Instant completedAt,
        @PositiveOrZero Long executionTimeMs,
        @DecimalMin("0.0") BigDecimal stepCost) {
}
