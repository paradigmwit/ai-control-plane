package com.fahdkhan.aicontrolplane.persistence.service;

import com.fahdkhan.aicontrolplane.model.StepStatus;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.entity.StepExecution;
import com.fahdkhan.aicontrolplane.persistence.repository.InstanceRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionStepRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.StepExecutionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StepExecutionService {

    private final StepExecutionRepository repository;
    private final InstanceRepository executionRepository;
    private final ExecutionStepRepository stepRepository;

    public StepExecutionService(
            StepExecutionRepository repository,
            InstanceRepository executionRepository,
            ExecutionStepRepository stepRepository) {
        this.repository = repository;
        this.executionRepository = executionRepository;
        this.stepRepository = stepRepository;
    }

    public StepExecutionDto save(StepExecutionDto dto) {
        return toDto(repository.save(toEntity(dto)));
    }

    public Optional<StepExecutionDto> findById(String stepExecutionId) {
        return repository.findById(stepExecutionId).map(this::toDto);
    }

    public List<StepExecutionDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public List<StepExecutionDto> findByInstanceId(String instanceId) {
        return repository.findByInstanceInstanceId(instanceId).stream().map(this::toDto).toList();
    }

    public void deleteById(String stepExecutionId) {
        repository.deleteById(stepExecutionId);
    }

    public Optional<StepExecutionDto> update(String stepExecutionId, StepExecutionDto dto) {
        if (!repository.existsById(stepExecutionId)) {
            return Optional.empty();
        }

        return Optional.of(save(new StepExecutionDto(
                stepExecutionId,
                dto.instanceId(),
                dto.stepId(),
                dto.status(),
                dto.outputPayload(),
                dto.errorMessage(),
                dto.startedAt(),
                dto.completedAt(),
                dto.executionTimeMs(),
                dto.stepCost())));
    }

    private StepExecution toEntity(StepExecutionDto dto) {
        StepExecution entity = new StepExecution();
        entity.setStepExecutionId(dto.stepExecutionId());
        entity.setInstance(executionRepository.getReferenceById(dto.instanceId()));
        entity.setStep(stepRepository.getReferenceById(dto.stepId()));
        entity.setStatus(StepStatus.valueOf(dto.status()));
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
                entity.getStepExecutionId(),
                entity.getInstance().getInstanceId(),
                entity.getStep().getStepId(),
                entity.getStatus().toString(),
                entity.getOutputPayload(),
                entity.getErrorMessage(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getExecutionTimeMs(),
                entity.getStepCost());
    }
}
