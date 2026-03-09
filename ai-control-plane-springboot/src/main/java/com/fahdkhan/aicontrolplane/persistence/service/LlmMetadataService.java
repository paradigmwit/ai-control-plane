package com.fahdkhan.aicontrolplane.persistence.service;

import com.fahdkhan.aicontrolplane.persistence.dto.LlmMetadataDto;
import com.fahdkhan.aicontrolplane.persistence.entity.LlmMetadata;
import com.fahdkhan.aicontrolplane.persistence.repository.InstanceRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.LlmMetadataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class LlmMetadataService {

    private final LlmMetadataRepository repository;
    private final InstanceRepository instanceRepository;

    public LlmMetadataService(
            LlmMetadataRepository repository,
            InstanceRepository instanceRepository) {
        this.repository = repository;
        this.instanceRepository = instanceRepository;
    }

    public LlmMetadataDto save(LlmMetadataDto dto) {
        return toDto(repository.save(toEntity(dto)));
    }

    public Optional<LlmMetadataDto> findById(String id) {
        return repository.findById(id).map(this::toDto);
    }

    public List<LlmMetadataDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private LlmMetadata toEntity(LlmMetadataDto dto) {
        LlmMetadata entity = new LlmMetadata();
        entity.setInstanceId(dto.instanceId());
        entity.setInstance(instanceRepository.getReferenceById(dto.instanceId()));
        entity.setProviderId(dto.providerId());
        entity.setModelName(dto.modelName());
        entity.setPromptTokens(dto.promptTokens());
        entity.setCompletionTokens(dto.completionTokens());
        entity.setLlmCost(dto.llmCost());
        entity.setRawResponse(dto.rawResponse());
        return entity;
    }

    private LlmMetadataDto toDto(LlmMetadata entity) {
        return new LlmMetadataDto(
                entity.getInstanceId(),
                entity.getProviderId(),
                entity.getModelName(),
                entity.getPromptTokens(),
                entity.getCompletionTokens(),
                entity.getLlmCost(),
                entity.getRawResponse());
    }
}
