package com.fahdkhan.aicontrolplane.persistence.dto;

import java.math.BigDecimal;

public record LlmMetadataDto(
        String executionId,
        String providerId,
        String modelName,
        Integer promptTokens,
        Integer completionTokens,
        BigDecimal llmCost,
        String rawResponse) {
}
