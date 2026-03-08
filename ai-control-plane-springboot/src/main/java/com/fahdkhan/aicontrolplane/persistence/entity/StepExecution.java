package com.fahdkhan.aicontrolplane.persistence.entity;

import com.fahdkhan.aicontrolplane.model.StepStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "step_execution", schema = "control_plane")
public class StepExecution {

    @EmbeddedId
    private StepExecutionId id;

    @Column(name = "plan_id", nullable = false)
    private String planId;

    @MapsId("executionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_id", nullable = false)
    private ExecutionInstance execution;

    @MapsId("stepId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", insertable = false, updatable = false),
        @JoinColumn(name = "step_id", referencedColumnName = "step_id", insertable = false, updatable = false)
    })
    private ExecutionStep step;

    @Column(name = "status", nullable = false)
    private StepStatus status;

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

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
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

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
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
