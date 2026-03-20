package com.fahdkhan.aicontrolplane.persistence.service;

import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.entity.Instance;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionPlanRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.InstanceRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExecutionInstanceService {

    private final InstanceRepository repository;
    private final ExecutionPlanRepository planRepository;
    private final InstanceRepository instanceRepository;

    public ExecutionInstanceService(
            InstanceRepository repository,
            ExecutionPlanRepository planRepository,
            InstanceRepository instanceRepository) {
        this.repository = repository;
        this.planRepository = planRepository;
        this.instanceRepository = instanceRepository;
    }

    public ExecutionInstanceDto save(ExecutionInstanceDto dto) {
        return toDto(repository.save(toEntity(dto)));
    }

    public Optional<ExecutionInstanceDto> findById(String id) {
        return repository.findById(id).map(this::toDto);
    }

    public List<ExecutionInstanceDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public Optional<ExecutionInstanceDto> update(String id, ExecutionInstanceDto dto) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }

        return Optional.of(save(new ExecutionInstanceDto(
                id,
                dto.planId(),
                dto.status(),
                dto.createdAt(),
                dto.startedAt(),
                dto.completedAt(),
                dto.totalCost(),
                dto.errorPayload())));
    }

    private Instance toEntity(ExecutionInstanceDto dto) {
        Instance entity = new Instance();
        entity.setInstanceId(dto.instanceId());
        entity.setPlan(planRepository.getReferenceById(dto.planId()));
        entity.setStatus(ExecutionStatus.valueOf(dto.status()));
        entity.setCreatedAt(dto.createdAt());
        entity.setStartedAt(dto.startedAt());
        entity.setCompletedAt(dto.completedAt());
        entity.setTotalCost(dto.totalCost());
        entity.setErrorPayload(dto.errorPayload());
        return entity;
    }

    private ExecutionInstanceDto toDto(Instance entity) {
        return new ExecutionInstanceDto(
                entity.getInstanceId(),
                entity.getPlan().getPlanId(),
                entity.getStatus().toString(),
                entity.getCreatedAt(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getTotalCost(),
                entity.getErrorPayload());
    }
}
