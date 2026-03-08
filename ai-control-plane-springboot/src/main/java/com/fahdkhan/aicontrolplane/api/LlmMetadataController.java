package com.fahdkhan.aicontrolplane.api;

import jakarta.validation.Valid;
import com.fahdkhan.aicontrolplane.persistence.dto.LlmMetadataDto;
import com.fahdkhan.aicontrolplane.persistence.service.LlmMetadataService;
import java.util.List;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/llm-metadata")
public class LlmMetadataController {

    private final LlmMetadataService llmMetadataService;

    public LlmMetadataController(LlmMetadataService llmMetadataService) {
        this.llmMetadataService = llmMetadataService;
    }

    @GetMapping
    public List<LlmMetadataDto> listLlmMetadata() {
        return llmMetadataService.findAll();
    }

    @GetMapping("/{executionId}")
    public ResponseEntity<LlmMetadataDto> getLlmMetadata(@PathVariable String executionId) {
        return llmMetadataService.findById(executionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LlmMetadataDto> createLlmMetadata(@Valid @RequestBody LlmMetadataDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(llmMetadataService.save(dto));
    }

    @PutMapping("/{executionId}")
    public ResponseEntity<LlmMetadataDto> upsertLlmMetadata(
            @PathVariable String executionId, @Valid @RequestBody LlmMetadataDto dto) {
        LlmMetadataDto payload = new LlmMetadataDto(
                executionId,
                dto.providerId(),
                dto.modelName(),
                dto.promptTokens(),
                dto.completionTokens(),
                dto.llmCost(),
                dto.rawResponse());
        return ResponseEntity.ok(llmMetadataService.save(payload));
    }

    @DeleteMapping("/{executionId}")
    public ResponseEntity<Void> deleteLlmMetadata(@PathVariable String executionId) {
        llmMetadataService.deleteById(executionId);
        return ResponseEntity.noContent().build();
    }
}
