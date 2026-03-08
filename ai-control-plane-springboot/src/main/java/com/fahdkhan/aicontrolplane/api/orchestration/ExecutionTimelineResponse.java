package com.fahdkhan.aicontrolplane.api.orchestration;

import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import java.util.List;

public record ExecutionTimelineResponse(String executionId, String status, List<StepExecutionDto> steps) {
}
