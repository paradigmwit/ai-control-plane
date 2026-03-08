package com.fahdkhan.aicontrolplane.api;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionStepDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionStepService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/execution-plans/{planId}/steps")
public class ExecutionStepController {

    private final ExecutionStepService executionStepService;

    public ExecutionStepController(ExecutionStepService executionStepService) {
        this.executionStepService = executionStepService;
    }

    @GetMapping
    public List<ExecutionStepDto> listExecutionSteps() {
        return executionStepService.findAll();
    }

    @GetMapping("/{stepId}")
    public ResponseEntity<ExecutionStepDto> getExecutionStep(@PathVariable String planId, @PathVariable String stepId) {
        return executionStepService.findById(planId, stepId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExecutionStepDto> createExecutionStep(
            @PathVariable String planId, @Valid @RequestBody ExecutionStepDto dto) {
        ExecutionStepDto payload = new ExecutionStepDto(dto.stepId(), planId, dto.toolName(), dto.inputPayload(), dto.metadata());
        return ResponseEntity.status(HttpStatus.CREATED).body(executionStepService.save(payload));
    }

    @PutMapping("/{stepId}")
    public ResponseEntity<ExecutionStepDto> upsertExecutionStep(
            @PathVariable String planId, @PathVariable String stepId, @Valid @RequestBody ExecutionStepDto dto) {
        ExecutionStepDto payload = new ExecutionStepDto(stepId, planId, dto.toolName(), dto.inputPayload(), dto.metadata());
        return ResponseEntity.ok(executionStepService.save(payload));
    }

    @DeleteMapping("/{stepId}")
    public ResponseEntity<Void> deleteExecutionStep(@PathVariable String planId, @PathVariable String stepId) {
        executionStepService.deleteById(planId, stepId);
        return ResponseEntity.noContent().build();
    }
}
