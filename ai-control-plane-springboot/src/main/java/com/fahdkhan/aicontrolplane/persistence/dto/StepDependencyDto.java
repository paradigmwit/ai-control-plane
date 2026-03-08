package com.fahdkhan.aicontrolplane.persistence.dto;

import jakarta.validation.constraints.NotBlank;

public record StepDependencyDto(@NotBlank String planId, @NotBlank String stepId, @NotBlank String dependsOnStepId) {
}
