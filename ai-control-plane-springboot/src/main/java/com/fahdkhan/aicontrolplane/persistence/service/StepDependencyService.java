package com.fahdkhan.aicontrolplane.persistence.service;

import com.fahdkhan.aicontrolplane.persistence.dto.StepDependencyDto;
import com.fahdkhan.aicontrolplane.persistence.entity.StepDependency;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionStepRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.StepDependencyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StepDependencyService {

    private final StepDependencyRepository repository;
    private final ExecutionStepRepository stepRepository;

    public StepDependencyService(StepDependencyRepository repository, ExecutionStepRepository stepRepository) {
        this.repository = repository;
        this.stepRepository = stepRepository;
    }

    public StepDependencyDto save(StepDependencyDto dto) {
        return toDto(repository.save(toEntity(dto)));
    }

    public Optional<StepDependencyDto> findById(String stepDependencyId) {
        return repository.findById(stepDependencyId).map(this::toDto);
    }

    public List<StepDependencyDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public List<StepDependencyDto> findByPlanId(String planId) {
        return repository.findByPlanPlanId(planId).stream().map(this::toDto).toList();
    }

    public void deleteById(String stepDependencyId) {
        repository.deleteById(stepDependencyId);
    }

    public Optional<StepDependencyDto> update(String stepDependencyId, StepDependencyDto dto) {
        if (!repository.existsById(stepDependencyId)) {
            return Optional.empty();
        }

        return Optional.of(save(new StepDependencyDto(dto.stepId(), dto.dependsOnStep())));
    }

    private StepDependency toEntity(StepDependencyDto dto) {
        StepDependency entity = new StepDependency();
        entity.setId(dto.stepId());
        entity.setStep(stepRepository.getReferenceById(dto.stepId()));
        entity.setDependsOnStep(stepRepository.getReferenceById(dto.dependsOnStep()));
        return entity;
    }

    private StepDependencyDto toDto(StepDependency entity) {
        return new StepDependencyDto(entity.getId(), entity.getDependsOnStep().getStepId());
    }
}
