package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "step_execution")
public class StepExecution {

    @EmbeddedId
    private StepExecutionId id;

    @MapsId("executionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_id", nullable = false)
    private ExecutionInstance execution;

    @MapsId("stepId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private ExecutionStep step;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "output_payload", columnDefinition = "json")
    private String outputPayload;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "step_cost", precision = 19, scale = 4)
    private BigDecimal stepCost;

    public StepExecutionId getId() {
        return id;
    }

    public void setId(StepExecutionId id) {
        this.id = id;
    }

    public ExecutionInstance getExecution() {
        return execution;
    }

    public void setExecution(ExecutionInstance execution) {
        this.execution = execution;
    }

    public ExecutionStep getStep() {
        return step;
    }

    public void setStep(ExecutionStep step) {
        this.step = step;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutputPayload() {
        return outputPayload;
    }

    public void setOutputPayload(String outputPayload) {
        this.outputPayload = outputPayload;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public BigDecimal getStepCost() {
        return stepCost;
    }

    public void setStepCost(BigDecimal stepCost) {
        this.stepCost = stepCost;
    }
}
