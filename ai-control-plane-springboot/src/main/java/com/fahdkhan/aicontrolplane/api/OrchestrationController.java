package com.fahdkhan.aicontrolplane.api;

import com.fahdkhan.aicontrolplane.api.orchestration.ExecutionTimelineResponse;
import com.fahdkhan.aicontrolplane.api.orchestration.PlanRequest;
import com.fahdkhan.aicontrolplane.api.orchestration.PlanResponse;
import com.fahdkhan.aicontrolplane.orchestration.ExecutionCommandService;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrchestrationController {

    private final ExecutionPlanService executionPlanService;
    private final ExecutionCommandService executionCommandService;

    public OrchestrationController(
            ExecutionPlanService executionPlanService, ExecutionCommandService executionCommandService) {
        this.executionPlanService = executionPlanService;
        this.executionCommandService = executionCommandService;
    }

    @PostMapping("/planning/plan")
    public ResponseEntity<PlanResponse> plan(@Valid @RequestBody PlanRequest request) {
        String planId = "plan-" + UUID.randomUUID();
        executionPlanService.save(new ExecutionPlanDto(planId, request.metadata(), Instant.now()));
        return ResponseEntity.ok(new PlanResponse(planId, "VALIDATED", "Plan accepted for execution"));
    }

    @PostMapping("/executions/{executionId}:start")
    public ResponseEntity<PlanResponse> startExecution(@PathVariable String executionId) {
        executionCommandService.start(executionId);
        return ResponseEntity.ok(new PlanResponse(executionId, "RUNNING", "Execution started"));
    }

    @PostMapping("/executions/{executionId}:cancel")
    public ResponseEntity<PlanResponse> cancelExecution(@PathVariable String executionId) {
        executionCommandService.cancel(executionId);
        return ResponseEntity.ok(new PlanResponse(executionId, "CANCELLED", "Execution cancelled"));
    }

    @PostMapping("/executions/{executionId}:retry-failed")
    public ResponseEntity<PlanResponse> retryExecution(@PathVariable String executionId) {
        executionCommandService.retryFailed(executionId);
        return ResponseEntity.ok(new PlanResponse(executionId, "RUNNING", "Execution retry started"));
    }

    @GetMapping("/executions/{executionId}/timeline")
    public ResponseEntity<ExecutionTimelineResponse> getTimeline(@PathVariable String executionId) {
        return ResponseEntity.ok(executionCommandService.timeline(executionId));
    }
}
