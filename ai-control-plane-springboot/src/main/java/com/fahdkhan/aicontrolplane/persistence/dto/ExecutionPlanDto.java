package com.fahdkhan.aicontrolplane.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ExecutionPlanDto(@NotBlank String planId, String metadata, @NotNull Instant createdAt) {
}
