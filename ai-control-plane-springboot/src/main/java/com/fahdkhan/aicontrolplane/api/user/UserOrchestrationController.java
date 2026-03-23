package com.fahdkhan.aicontrolplane.api.user;

import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
import com.fahdkhan.aicontrolplane.user.CurrencyConversionWorkflowService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
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
    private final CurrencyConversionWorkflowService currencyConversionWorkflowService;

    public UserOrchestrationController(
            ExecutionPlanService executionPlanService,
            ExecutionInstanceService executionInstanceService,
            StepExecutionService stepExecutionService,
            CurrencyConversionWorkflowService currencyConversionWorkflowService) {
        this.executionPlanService = executionPlanService;
        this.executionInstanceService = executionInstanceService;
        this.stepExecutionService = stepExecutionService;
        this.currencyConversionWorkflowService = currencyConversionWorkflowService;
    }

    @PostMapping("/workflows/currency-conversion/execute")
    public ResponseEntity<CurrencyConversionWorkflowResponse> executeCurrencyConversionWorkflow(
            @RequestBody CurrencyConversionWorkflowRequest request) {
        try {
            return ResponseEntity.ok(currencyConversionWorkflowService.execute(request.prompt()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/workflows/currency-conversion/history")
    public List<ExecutionInstanceDto> currencyConversionWorkflowHistory() {
        return executionInstanceService.findAll().stream()
                .filter(instance -> instance.planId().equals(CurrencyConversionWorkflowService.PLAN_ID))
                .sorted(Comparator.comparing(
                                ExecutionInstanceDto::createdAt,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed())
                .toList();
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
                BigDecimal.ZERO,
                null));
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
    public ResponseEntity<StepExecutionDto> markStepCompleted(@PathVariable String instanceId, @PathVariable String stepId) {
        try {
            return stepExecutionService.completeStepExecution(instanceId, stepId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(409).build();
        }
    }
}
