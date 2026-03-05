package com.fahdkhan.aicontrolplane.planner;

public record LlmMetadata(

        String providerId,
        String modelName,
        int promptTokens,
        int completionTokens,
        double cost,
        String rawResponse
) {
}