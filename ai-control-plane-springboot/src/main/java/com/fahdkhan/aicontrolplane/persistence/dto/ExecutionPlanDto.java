package com.fahdkhan.aicontrolplane.persistence.dto;

import java.time.Instant;

public record ExecutionPlanDto(String planId, String metadata, Instant createdAt) {
}
