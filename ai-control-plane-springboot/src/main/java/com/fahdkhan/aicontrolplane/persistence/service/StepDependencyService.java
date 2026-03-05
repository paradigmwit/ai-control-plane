package com.fahdkhan.aicontrolplane.persistence.service;

import com.fahdkhan.aicontrolplane.persistence.dto.StepDependencyDto;
import com.fahdkhan.aicontrolplane.persistence.entity.StepDependency;
import com.fahdkhan.aicontrolplane.persistence.entity.StepDependencyId;
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

    public Optional<StepDependencyDto> findById(String stepId, String dependsOnStepId) {
        return repository.findById(new StepDependencyId(stepId, dependsOnStepId)).map(this::toDto);
    }

    public List<StepDependencyDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public void deleteById(String stepId, String dependsOnStepId) {
        repository.deleteById(new StepDependencyId(stepId, dependsOnStepId));
    }

    private StepDependency toEntity(StepDependencyDto dto) {
        StepDependency entity = new StepDependency();
        entity.setId(new StepDependencyId(dto.stepId(), dto.dependsOnStepId()));
        entity.setStep(stepRepository.getReferenceById(dto.stepId()));
        entity.setDependsOnStep(stepRepository.getReferenceById(dto.dependsOnStepId()));
        return entity;
    }

    private StepDependencyDto toDto(StepDependency entity) {
        return new StepDependencyDto(entity.getId().getStepId(), entity.getId().getDependsOnStepId());
    }
}
