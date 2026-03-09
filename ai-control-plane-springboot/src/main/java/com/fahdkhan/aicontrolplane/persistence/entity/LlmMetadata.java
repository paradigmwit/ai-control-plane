package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "llm_metadata", schema = "control_plane")
@Getter
@Setter
public class LlmMetadata {

    @Id
    @Column(name = "instance_id", nullable = false)
    private String instanceId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "instance_id", nullable = false)
    private Instance instance;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "prompt_tokens", nullable = false)
    private Integer promptTokens;

    @Column(name = "completion_tokens", nullable = false)
    private Integer completionTokens;

    @Column(name = "llm_cost", precision = 19, scale = 4)
    private BigDecimal llmCost;

    @Column(name = "raw_response", columnDefinition = "text")
    private String rawResponse;
}
