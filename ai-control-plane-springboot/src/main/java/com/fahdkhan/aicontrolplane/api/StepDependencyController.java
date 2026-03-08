package com.fahdkhan.aicontrolplane.api;

import com.fahdkhan.aicontrolplane.persistence.dto.StepDependencyDto;
import com.fahdkhan.aicontrolplane.persistence.service.StepDependencyService;
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
@RequestMapping("/api/v1/execution-plans/{planId}/step-dependencies")
public class StepDependencyController {

    private final StepDependencyService stepDependencyService;

    public StepDependencyController(StepDependencyService stepDependencyService) {
        this.stepDependencyService = stepDependencyService;
    }

    @GetMapping
    public List<StepDependencyDto> listStepDependencies() {
        return stepDependencyService.findAll();
    }

    @GetMapping("/{stepId}/depends-on/{dependsOnStepId}")
    public ResponseEntity<StepDependencyDto> getStepDependency(
            @PathVariable String planId, @PathVariable String stepId, @PathVariable String dependsOnStepId) {
        return stepDependencyService.findById(planId, stepId, dependsOnStepId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StepDependencyDto> createStepDependency(
            @PathVariable String planId, @Valid @RequestBody StepDependencyDto dto) {
        StepDependencyDto payload = new StepDependencyDto(planId, dto.stepId(), dto.dependsOnStepId());
        return ResponseEntity.status(HttpStatus.CREATED).body(stepDependencyService.save(payload));
    }

    @PutMapping("/{stepId}/depends-on/{dependsOnStepId}")
    public ResponseEntity<StepDependencyDto> upsertStepDependency(
            @PathVariable String planId,
            @PathVariable String stepId,
            @PathVariable String dependsOnStepId,
            @Valid @RequestBody StepDependencyDto dto) {
        StepDependencyDto payload = new StepDependencyDto(planId, stepId, dependsOnStepId);
        return ResponseEntity.ok(stepDependencyService.save(payload));
    }

    @DeleteMapping("/{stepId}/depends-on/{dependsOnStepId}")
    public ResponseEntity<Void> deleteStepDependency(
            @PathVariable String planId, @PathVariable String stepId, @PathVariable String dependsOnStepId) {
        stepDependencyService.deleteById(planId, stepId, dependsOnStepId);
        return ResponseEntity.noContent().build();
    }
}
