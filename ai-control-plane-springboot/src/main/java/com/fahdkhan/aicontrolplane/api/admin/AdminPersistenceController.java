package com.fahdkhan.aicontrolplane.api.admin;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDetailsDto;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionStepDto;
import com.fahdkhan.aicontrolplane.persistence.dto.LlmMetadataDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepDependencyDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionStepService;
import com.fahdkhan.aicontrolplane.persistence.service.LlmMetadataService;
import com.fahdkhan.aicontrolplane.persistence.service.StepDependencyService;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
import java.util.List;
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
@RequestMapping("/api/admin")
public class AdminPersistenceController {

    private final ExecutionPlanService executionPlanService;
    private final ExecutionStepService executionStepService;
    private final StepDependencyService stepDependencyService;
    private final ExecutionInstanceService executionInstanceService;
    private final StepExecutionService stepExecutionService;
    private final LlmMetadataService llmMetadataService;

    public AdminPersistenceController(
            ExecutionPlanService executionPlanService,
            ExecutionStepService executionStepService,
            StepDependencyService stepDependencyService,
            ExecutionInstanceService executionInstanceService,
            StepExecutionService stepExecutionService,
            LlmMetadataService llmMetadataService) {
        this.executionPlanService = executionPlanService;
        this.executionStepService = executionStepService;
        this.stepDependencyService = stepDependencyService;
        this.executionInstanceService = executionInstanceService;
        this.stepExecutionService = stepExecutionService;
        this.llmMetadataService = llmMetadataService;
    }

    @PostMapping("/plans")
    public ExecutionPlanDto createPlan(@RequestBody ExecutionPlanDto dto) {
        return executionPlanService.save(dto);
    }

    @GetMapping("/plans")
    public List<ExecutionPlanDto> listPlans() {
        return executionPlanService.findAll();
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<ExecutionPlanDto> getPlan(@PathVariable String id) {
        return executionPlanService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<ExecutionPlanDto> updatePlan(@PathVariable String id, @RequestBody ExecutionPlanDto dto) {
        return executionPlanService.update(id, dto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable String id) {
        executionPlanService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/steps")
    public ExecutionStepDto createStep(@RequestBody ExecutionStepDto dto) {
        return executionStepService.save(dto);
    }

    @GetMapping("/steps")
    public List<ExecutionStepDto> listSteps() {
        return executionStepService.findAll();
    }

    @GetMapping("/steps/{id}")
    public ResponseEntity<ExecutionStepDto> getStep(@PathVariable String id) {
        return executionStepService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/steps/{id}")
    public ResponseEntity<ExecutionStepDto> updateStep(@PathVariable String id, @RequestBody ExecutionStepDto dto) {
        return executionStepService.update(id, dto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/steps/{id}")
    public ResponseEntity<Void> deleteStep(@PathVariable String id) {
        executionStepService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/step-dependencies")
    public StepDependencyDto createDependency(@RequestBody StepDependencyDto dto) {
        return stepDependencyService.save(dto);
    }

    @GetMapping("/step-dependencies")
    public List<StepDependencyDto> listDependencies() {
        return stepDependencyService.findAll();
    }

    @GetMapping("/step-dependencies/{id}")
    public ResponseEntity<StepDependencyDto> getDependency(@PathVariable String id) {
        return stepDependencyService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/step-dependencies/{id}")
    public ResponseEntity<StepDependencyDto> updateDependency(@PathVariable String id, @RequestBody StepDependencyDto dto) {
        return stepDependencyService.update(id, dto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/step-dependencies/{id}")
    public ResponseEntity<Void> deleteDependency(@PathVariable String id) {
        stepDependencyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/instances")
    public ExecutionInstanceDto createInstance(@RequestBody ExecutionInstanceDto dto) {
        return executionInstanceService.save(dto);
    }

    @GetMapping("/instances")
    public List<ExecutionInstanceDto> listInstances() {
        return executionInstanceService.findAll();
    }

    @GetMapping("/instances/{id}")
    public ResponseEntity<ExecutionInstanceDetailsDto> getInstance(@PathVariable String id) {
        return executionInstanceService.findById(id)
                .map(instance -> {
                    ExecutionPlanDto plan = executionPlanService.findById(instance.planId()).orElse(null);
                    List<ExecutionStepDto> steps = executionStepService.findByPlanId(instance.planId());
                    List<StepDependencyDto> dependencies = stepDependencyService.findByPlanId(instance.planId());
                    List<StepExecutionDto> stepExecutions = stepExecutionService.findByInstanceId(id);
                    LlmMetadataDto llmMetadata = llmMetadataService.findById(id).orElse(null);

                    return ResponseEntity.ok(new ExecutionInstanceDetailsDto(
                            instance,
                            plan,
                            steps,
                            dependencies,
                            stepExecutions,
                            llmMetadata));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/instances/{id}")
    public ResponseEntity<ExecutionInstanceDto> updateInstance(@PathVariable String id, @RequestBody ExecutionInstanceDto dto) {
        return executionInstanceService.update(id, dto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/instances/{id}")
    public ResponseEntity<Void> deleteInstance(@PathVariable String id) {
        executionInstanceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/step-executions")
    public StepExecutionDto createStepExecution(@RequestBody StepExecutionDto dto) {
        return stepExecutionService.save(dto);
    }

    @GetMapping("/step-executions")
    public List<StepExecutionDto> listStepExecutions() {
        return stepExecutionService.findAll();
    }

    @GetMapping("/step-executions/{id}")
    public ResponseEntity<StepExecutionDto> getStepExecution(@PathVariable String id) {
        return stepExecutionService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/step-executions/{id}")
    public ResponseEntity<StepExecutionDto> updateStepExecution(@PathVariable String id, @RequestBody StepExecutionDto dto) {
        return stepExecutionService.update(id, dto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/step-executions/{id}")
    public ResponseEntity<Void> deleteStepExecution(@PathVariable String id) {
        stepExecutionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/llm-metadata")
    public LlmMetadataDto createLlmMetadata(@RequestBody LlmMetadataDto dto) {
        return llmMetadataService.save(dto);
    }

    @GetMapping("/llm-metadata")
    public List<LlmMetadataDto> listLlmMetadata() {
        return llmMetadataService.findAll();
    }

    @GetMapping("/llm-metadata/{id}")
    public ResponseEntity<LlmMetadataDto> getLlmMetadata(@PathVariable String id) {
        return llmMetadataService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/llm-metadata/{id}")
    public ResponseEntity<LlmMetadataDto> updateLlmMetadata(@PathVariable String id, @RequestBody LlmMetadataDto dto) {
        return llmMetadataService.update(id, dto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/llm-metadata/{id}")
    public ResponseEntity<Void> deleteLlmMetadata(@PathVariable String id) {
        llmMetadataService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
