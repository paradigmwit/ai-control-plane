package com.fahdkhan.aicontrolplane.execution;

import com.fahdkhan.aicontrolplane.model.StepStatus;

import java.time.Instant;

public record StepExecution(

        String stepId,

        StepStatus status,

        Object output,

        String errorMessage,

        Instant startedAt,
        Instant completedAt,

        long executionTimeMs
) {
}