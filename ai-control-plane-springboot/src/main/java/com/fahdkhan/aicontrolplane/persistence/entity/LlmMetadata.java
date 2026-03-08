package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "llm_metadata", schema = "control_plane")
public class LlmMetadata {

    @Id
    @Column(name = "execution_id", nullable = false)
    private String executionId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "execution_id", nullable = false)
    private ExecutionInstance execution;

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

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public ExecutionInstance getExecution() {
        return execution;
    }

    public void setExecution(ExecutionInstance execution) {
        this.execution = execution;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public BigDecimal getLlmCost() {
        return llmCost;
    }

    public void setLlmCost(BigDecimal llmCost) {
        this.llmCost = llmCost;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }
}
