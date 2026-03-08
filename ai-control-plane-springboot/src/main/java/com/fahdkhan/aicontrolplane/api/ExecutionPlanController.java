package com.fahdkhan.aicontrolplane.api;

import jakarta.validation.Valid;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/execution-plans")
public class ExecutionPlanController {

    private final ExecutionPlanService executionPlanService;

    public ExecutionPlanController(ExecutionPlanService executionPlanService) {
        this.executionPlanService = executionPlanService;
    }

    @GetMapping
    public List<ExecutionPlanDto> listExecutionPlans() {
        return executionPlanService.findAll();
    }

    @GetMapping("/{planId}")
    public ResponseEntity<ExecutionPlanDto> getExecutionPlan(@PathVariable String planId) {
        return executionPlanService.findById(planId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExecutionPlanDto> createExecutionPlan(@Valid @RequestBody ExecutionPlanDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(executionPlanService.save(dto));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<ExecutionPlanDto> upsertExecutionPlan(
            @PathVariable String planId, @Valid @RequestBody ExecutionPlanDto dto) {
        ExecutionPlanDto payload = new ExecutionPlanDto(planId, dto.metadata(), dto.createdAt());
        return ResponseEntity.ok(executionPlanService.save(payload));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteExecutionPlan(@PathVariable String planId) {
        executionPlanService.deleteById(planId);
        return ResponseEntity.noContent().build();
    }
}
