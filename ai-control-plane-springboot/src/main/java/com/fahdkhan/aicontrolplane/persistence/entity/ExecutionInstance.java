package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "execution_instance")
public class ExecutionInstance {

    @Id
    @Column(name = "execution_id", nullable = false)
    private String executionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private ExecutionPlan plan;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "total_cost", precision = 19, scale = 4)
    private BigDecimal totalCost;

    @OneToMany(mappedBy = "execution")
    private Set<StepExecution> stepExecutions = new LinkedHashSet<>();

    @OneToOne(mappedBy = "execution")
    private LlmMetadata llmMetadata;

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public ExecutionPlan getPlan() {
        return plan;
    }

    public void setPlan(ExecutionPlan plan) {
        this.plan = plan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Set<StepExecution> getStepExecutions() {
        return stepExecutions;
    }

    public void setStepExecutions(Set<StepExecution> stepExecutions) {
        this.stepExecutions = stepExecutions;
    }

    public LlmMetadata getLlmMetadata() {
        return llmMetadata;
    }

    public void setLlmMetadata(LlmMetadata llmMetadata) {
        this.llmMetadata = llmMetadata;
    }
}
