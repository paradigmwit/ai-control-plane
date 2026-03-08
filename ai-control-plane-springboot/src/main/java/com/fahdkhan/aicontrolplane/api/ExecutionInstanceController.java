package com.fahdkhan.aicontrolplane.api;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
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
@RequestMapping("/api/v1/execution-instances")
public class ExecutionInstanceController {

    private final ExecutionInstanceService executionInstanceService;

    public ExecutionInstanceController(ExecutionInstanceService executionInstanceService) {
        this.executionInstanceService = executionInstanceService;
    }

    @GetMapping
    public List<ExecutionInstanceDto> listExecutionInstances() {
        return executionInstanceService.findAll();
    }

    @GetMapping("/{executionId}")
    public ResponseEntity<ExecutionInstanceDto> getExecutionInstance(@PathVariable String executionId) {
        return executionInstanceService.findById(executionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExecutionInstanceDto> createExecutionInstance(@RequestBody ExecutionInstanceDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(executionInstanceService.save(dto));
    }

    @PutMapping("/{executionId}")
    public ResponseEntity<ExecutionInstanceDto> upsertExecutionInstance(
            @PathVariable String executionId, @RequestBody ExecutionInstanceDto dto) {
        ExecutionInstanceDto payload = new ExecutionInstanceDto(
                executionId,
                dto.planId(),
                dto.status(),
                dto.createdAt(),
                dto.startedAt(),
                dto.completedAt(),
                dto.totalCost());
        return ResponseEntity.ok(executionInstanceService.save(payload));
    }

    @DeleteMapping("/{executionId}")
    public ResponseEntity<Void> deleteExecutionInstance(@PathVariable String executionId) {
        executionInstanceService.deleteById(executionId);
        return ResponseEntity.noContent().build();
    }
}
