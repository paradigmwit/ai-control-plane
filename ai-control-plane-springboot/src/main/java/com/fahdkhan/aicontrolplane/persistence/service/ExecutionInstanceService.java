package com.fahdkhan.aicontrolplane.persistence.service;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionInstance;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionInstanceRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionPlanRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExecutionInstanceService {

    private final ExecutionInstanceRepository repository;
    private final ExecutionPlanRepository planRepository;

    public ExecutionInstanceService(ExecutionInstanceRepository repository, ExecutionPlanRepository planRepository) {
        this.repository = repository;
        this.planRepository = planRepository;
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

    private ExecutionInstance toEntity(ExecutionInstanceDto dto) {
        ExecutionInstance entity = new ExecutionInstance();
        entity.setExecutionId(dto.executionId());
        entity.setPlan(planRepository.getReferenceById(dto.planId()));
        entity.setStatus(dto.status());
        entity.setCreatedAt(dto.createdAt());
        entity.setStartedAt(dto.startedAt());
        entity.setCompletedAt(dto.completedAt());
        entity.setTotalCost(dto.totalCost());
        return entity;
    }

    private ExecutionInstanceDto toDto(ExecutionInstance entity) {
        return new ExecutionInstanceDto(
                entity.getExecutionId(),
                entity.getPlan().getPlanId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getTotalCost());
    }
}
