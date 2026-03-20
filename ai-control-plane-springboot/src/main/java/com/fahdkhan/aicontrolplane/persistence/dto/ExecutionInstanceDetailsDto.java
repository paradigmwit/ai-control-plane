package com.fahdkhan.aicontrolplane.persistence.dto;

import java.util.List;

public record ExecutionInstanceDetailsDto(
        ExecutionInstanceDto instance,
        ExecutionPlanDto plan,
        List<ExecutionStepDto> steps,
        List<StepDependencyDto> stepDependencies,
        List<StepExecutionDto> stepExecutions,
        LlmMetadataDto llmMetadata) {
}
