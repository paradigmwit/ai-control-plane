package com.fahdkhan.aicontrolplane.persistence.service;

import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.entity.Plan;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionPlanRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExecutionPlanService {

    private final ExecutionPlanRepository repository;

    public ExecutionPlanService(ExecutionPlanRepository repository) {
        this.repository = repository;
    }

    public ExecutionPlanDto save(ExecutionPlanDto dto) {
        return toDto(repository.save(toEntity(dto)));
    }

    public Optional<ExecutionPlanDto> findById(String id) {
        return repository.findById(id).map(this::toDto);
    }

    public List<ExecutionPlanDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Plan toEntity(ExecutionPlanDto dto) {
        Plan entity = new Plan();
        entity.setPlanId(dto.planId());
        entity.setMetadata(dto.metadata());
        entity.setCreatedAt(dto.createdAt());
        return entity;
    }

    private ExecutionPlanDto toDto(Plan entity) {
        return new ExecutionPlanDto(entity.getPlanId(), entity.getMetadata(), entity.getCreatedAt());
    }
}
