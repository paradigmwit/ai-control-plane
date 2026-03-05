package com.fahdkhan.aicontrolplane.persistence.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ExecutionInstanceDto(
        String executionId,
        String planId,
        String status,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt,
        BigDecimal totalCost) {
}
