package com.fahdkhan.aicontrolplane.persistence.service;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionStepDto;
import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionStep;
import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionStepId;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionPlanRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionStepRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExecutionStepService {

    private final ExecutionStepRepository repository;
    private final ExecutionPlanRepository planRepository;

    public ExecutionStepService(ExecutionStepRepository repository, ExecutionPlanRepository planRepository) {
        this.repository = repository;
        this.planRepository = planRepository;
    }

    public ExecutionStepDto save(ExecutionStepDto dto) {
        return toDto(repository.save(toEntity(dto)));
    }

    public Optional<ExecutionStepDto> findById(String planId, String stepId) {
        return repository.findById(new ExecutionStepId(planId, stepId)).map(this::toDto);
    }

    public List<ExecutionStepDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public void deleteById(String planId, String stepId) {
        repository.deleteById(new ExecutionStepId(planId, stepId));
    }

    private ExecutionStep toEntity(ExecutionStepDto dto) {
        ExecutionStep entity = new ExecutionStep();
        entity.setId(new ExecutionStepId(dto.planId(), dto.stepId()));
        entity.setPlan(planRepository.getReferenceById(dto.planId()));
        entity.setToolName(dto.toolName());
        entity.setInputPayload(dto.inputPayload());
        entity.setMetadata(dto.metadata());
        return entity;
    }

    private ExecutionStepDto toDto(ExecutionStep entity) {
        return new ExecutionStepDto(
                entity.getId().getStepId(),
                entity.getId().getPlanId(),
                entity.getToolName(),
                entity.getInputPayload(),
                entity.getMetadata());
    }
}
