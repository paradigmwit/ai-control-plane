package com.fahdkhan.aicontrolplane.persistence.dto;

import jakarta.validation.constraints.NotBlank;

public record ExecutionStepDto(
        @NotBlank String stepId,
        @NotBlank String planId,
        @NotBlank String toolName,
        String inputPayload,
        String metadata) {
}
