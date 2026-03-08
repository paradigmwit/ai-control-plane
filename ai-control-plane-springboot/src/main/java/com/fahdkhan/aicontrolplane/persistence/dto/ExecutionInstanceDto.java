package com.fahdkhan.aicontrolplane.persistence.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.Instant;

public record ExecutionInstanceDto(
        @NotBlank String executionId,
        @NotBlank String planId,
        @NotBlank @Pattern(regexp = "CREATED|VALIDATED|POLICY_APPROVED|RUNNING|COMPLETED|FAILED|CANCELLED") String status,
        @NotNull Instant createdAt,
        Instant startedAt,
        Instant completedAt,
        @DecimalMin("0.0") BigDecimal totalCost) {
}
