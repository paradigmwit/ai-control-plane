package com.fahdkhan.aicontrolplane.persistence.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record LlmMetadataDto(
        @NotBlank String executionId,
        @NotBlank String providerId,
        @NotBlank String modelName,
        @PositiveOrZero Integer promptTokens,
        @PositiveOrZero Integer completionTokens,
        @DecimalMin("0.0") BigDecimal llmCost,
        String rawResponse) {
}
