package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "execution_plan")
public class ExecutionPlan {

    @Id
    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Column(name = "metadata", columnDefinition = "json")
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "plan")
    private Set<ExecutionStep> steps = new LinkedHashSet<>();

    @OneToMany(mappedBy = "plan")
    private Set<ExecutionInstance> executions = new LinkedHashSet<>();

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<ExecutionStep> getSteps() {
        return steps;
    }

    public void setSteps(Set<ExecutionStep> steps) {
        this.steps = steps;
    }

    public Set<ExecutionInstance> getExecutions() {
        return executions;
    }

    public void setExecutions(Set<ExecutionInstance> executions) {
        this.executions = executions;
    }
}
