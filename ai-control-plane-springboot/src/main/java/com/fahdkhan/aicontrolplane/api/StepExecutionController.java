package com.fahdkhan.aicontrolplane.api;

import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
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
@RequestMapping("/api/v1/step-executions")
public class StepExecutionController {

    private final StepExecutionService stepExecutionService;

    public StepExecutionController(StepExecutionService stepExecutionService) {
        this.stepExecutionService = stepExecutionService;
    }

    @GetMapping
    public List<StepExecutionDto> listStepExecutions() {
        return stepExecutionService.findAll();
    }

    @GetMapping("/{executionId}/steps/{stepId}")
    public ResponseEntity<StepExecutionDto> getStepExecution(
            @PathVariable String executionId, @PathVariable String stepId) {
        return stepExecutionService.findById(executionId, stepId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StepExecutionDto> createStepExecution(@RequestBody StepExecutionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stepExecutionService.save(dto));
    }

    @PutMapping("/{executionId}/steps/{stepId}")
    public ResponseEntity<StepExecutionDto> upsertStepExecution(
            @PathVariable String executionId, @PathVariable String stepId, @RequestBody StepExecutionDto dto) {
        StepExecutionDto payload = new StepExecutionDto(
                executionId,
                stepId,
                dto.status(),
                dto.outputPayload(),
                dto.errorMessage(),
                dto.startedAt(),
                dto.completedAt(),
                dto.executionTimeMs(),
                dto.stepCost());
        return ResponseEntity.ok(stepExecutionService.save(payload));
    }

    @DeleteMapping("/{executionId}/steps/{stepId}")
    public ResponseEntity<Void> deleteStepExecution(@PathVariable String executionId, @PathVariable String stepId) {
        stepExecutionService.deleteById(executionId, stepId);
        return ResponseEntity.noContent().build();
    }
}
