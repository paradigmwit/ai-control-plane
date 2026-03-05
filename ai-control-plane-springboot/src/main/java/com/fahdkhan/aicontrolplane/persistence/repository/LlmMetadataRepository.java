package com.fahdkhan.aicontrolplane.persistence.repository;

import com.fahdkhan.aicontrolplane.persistence.entity.LlmMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LlmMetadataRepository extends JpaRepository<LlmMetadata, String> {
}
