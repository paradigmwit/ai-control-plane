package com.fahdkhan.aicontrolplane.execution;

import com.fahdkhan.aicontrolplane.model.ExecutionStatus;

import java.time.Instant;
import java.util.Map;

public record ExecutionInstance(

        String executionId,
        String planId,

        ExecutionStatus status,

        Instant createdAt,
        Instant startedAt,
        Instant completedAt,

        Map<String, StepExecution> stepExecutions,

        CostBreakdown cost
) {
}
