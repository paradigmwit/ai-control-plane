package com.fahdkhan.aicontrolplane.persistence.service;

import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionStepId;
import com.fahdkhan.aicontrolplane.persistence.entity.StepExecution;
import com.fahdkhan.aicontrolplane.persistence.entity.StepExecutionId;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionInstanceRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionPlanRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionStepRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.StepExecutionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StepExecutionService {

    private final StepExecutionRepository repository;
    private final ExecutionInstanceRepository executionRepository;
    private final ExecutionStepRepository stepRepository;
    private final ExecutionPlanRepository planRepository;

    public StepExecutionService(
            StepExecutionRepository repository,
            ExecutionInstanceRepository executionRepository,
            ExecutionStepRepository stepRepository,
            ExecutionPlanRepository planRepository) {
        this.repository = repository;
        this.executionRepository = executionRepository;
        this.stepRepository = stepRepository;
        this.planRepository = planRepository;
    }

    public StepExecutionDto save(StepExecutionDto dto) {
        return toDto(repository.save(toEntity(dto)));
    }

    public Optional<StepExecutionDto> findById(String executionId, String stepId) {
        return repository.findById(new StepExecutionId(executionId, stepId)).map(this::toDto);
    }

    public List<StepExecutionDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public void deleteById(String executionId, String stepId) {
        repository.deleteById(new StepExecutionId(executionId, stepId));
    }

    private StepExecution toEntity(StepExecutionDto dto) {
        StepExecution entity = new StepExecution();
        entity.setId(new StepExecutionId(dto.executionId(), dto.stepId()));
        entity.setExecution(executionRepository.getReferenceById(dto.executionId()));
        entity.setPlan(planRepository.getReferenceById(dto.planId()));
        entity.setStep(stepRepository.getReferenceById(new ExecutionStepId(dto.planId(), dto.stepId())));
        entity.setStatus(dto.status());
        entity.setOutputPayload(dto.outputPayload());
        entity.setErrorMessage(dto.errorMessage());
        entity.setStartedAt(dto.startedAt());
        entity.setCompletedAt(dto.completedAt());
        entity.setExecutionTimeMs(dto.executionTimeMs());
        entity.setStepCost(dto.stepCost());
        return entity;
    }

    private StepExecutionDto toDto(StepExecution entity) {
        return new StepExecutionDto(
                entity.getId().getExecutionId(),
                entity.getPlan().getPlanId(),
                entity.getId().getStepId(),
                entity.getStatus(),
                entity.getOutputPayload(),
                entity.getErrorMessage(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getExecutionTimeMs(),
                entity.getStepCost());
    }
}
