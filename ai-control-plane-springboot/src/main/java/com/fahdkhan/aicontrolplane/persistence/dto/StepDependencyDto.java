package com.fahdkhan.aicontrolplane.persistence.dto;

public record StepDependencyDto(String planId, String stepId, String dependsOnStepId) {
}
