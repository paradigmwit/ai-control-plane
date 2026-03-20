package com.fahdkhan.aicontrolplane.persistence.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ExecutionInstanceDto(
        String instanceId,
        String planId,
        String status,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt,
        BigDecimal totalCost,
        String errorPayload) {
}
