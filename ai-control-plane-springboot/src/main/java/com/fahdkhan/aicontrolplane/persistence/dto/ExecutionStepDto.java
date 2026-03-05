package com.fahdkhan.aicontrolplane.persistence.dto;

public record ExecutionStepDto(String stepId, String planId, String toolName, String inputPayload, String metadata) {
}
