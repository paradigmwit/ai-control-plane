package com.fahdkhan.aicontrolplane.api;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionStepDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionStepService;
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
@RequestMapping("/api/v1/execution-steps")
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
    public ResponseEntity<ExecutionStepDto> getExecutionStep(@PathVariable String stepId) {
        return executionStepService.findById(stepId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExecutionStepDto> createExecutionStep(@RequestBody ExecutionStepDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(executionStepService.save(dto));
    }

    @PutMapping("/{stepId}")
    public ResponseEntity<ExecutionStepDto> upsertExecutionStep(
            @PathVariable String stepId, @RequestBody ExecutionStepDto dto) {
        ExecutionStepDto payload =
                new ExecutionStepDto(stepId, dto.planId(), dto.toolName(), dto.inputPayload(), dto.metadata());
        return ResponseEntity.ok(executionStepService.save(payload));
    }

    @DeleteMapping("/{stepId}")
    public ResponseEntity<Void> deleteExecutionStep(@PathVariable String stepId) {
        executionStepService.deleteById(stepId);
        return ResponseEntity.noContent().build();
    }
}
