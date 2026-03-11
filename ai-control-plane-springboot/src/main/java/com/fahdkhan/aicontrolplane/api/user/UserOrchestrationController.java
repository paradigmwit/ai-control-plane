package com.fahdkhan.aicontrolplane.api.user;

import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.model.StepStatus;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/orchestration")
public class UserOrchestrationController {

    private final ExecutionPlanService executionPlanService;
    private final ExecutionInstanceService executionInstanceService;
    private final StepExecutionService stepExecutionService;

    public UserOrchestrationController(
            ExecutionPlanService executionPlanService,
            ExecutionInstanceService executionInstanceService,
            StepExecutionService stepExecutionService) {
        this.executionPlanService = executionPlanService;
        this.executionInstanceService = executionInstanceService;
        this.stepExecutionService = stepExecutionService;
    }

    @PostMapping("/plan")
    public ExecutionPlanDto createPlan(@RequestBody UserOrchestrationRequest request, Authentication authentication) {
        String planId = "plan-" + UUID.randomUUID();
        String metadata = "{\"taskSummary\":\"" + request.taskSummary() + "\",\"createdBy\":\""
                + authentication.getName() + "\",\"metadata\":\"" + request.metadata() + "\"}";
        return executionPlanService.save(new ExecutionPlanDto(planId, metadata, Instant.now()));
    }

    @PostMapping("/execute/{planId}")
    public ExecutionInstanceDto executePlan(@PathVariable String planId) {
        String instanceId = "instance-" + UUID.randomUUID();
        return executionInstanceService.save(new ExecutionInstanceDto(
                instanceId,
                planId,
                ExecutionStatus.RUNNING.name(),
                Instant.now(),
                Instant.now(),
                null,
                BigDecimal.ZERO));
    }

    @GetMapping("/executions/{instanceId}")
    public ResponseEntity<ExecutionInstanceDto> executionStatus(@PathVariable String instanceId) {
        return executionInstanceService.findById(instanceId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/executions/{instanceId}/steps")
    public List<StepExecutionDto> executionSteps(@PathVariable String instanceId) {
        return stepExecutionService.findAll().stream()
                .filter(step -> step.instanceId().equals(instanceId))
                .toList();
    }

    @PostMapping("/executions/{instanceId}/steps/{stepId}/complete")
    public StepExecutionDto markStepCompleted(@PathVariable String instanceId, @PathVariable String stepId) {
        return stepExecutionService.save(new StepExecutionDto(
                "step-exec-" + UUID.randomUUID(),
                instanceId,
                stepId,
                StepStatus.COMPLETED.name(),
                "{}",
                null,
                Instant.now(),
                Instant.now(),
                0L,
                BigDecimal.ZERO));
    }
}
