package com.fahdkhan.aicontrolplane.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "execution_step")
public class ExecutionStep {

    @Id
    @Column(name = "step_id", nullable = false)
    private String stepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private ExecutionPlan plan;

    @Column(name = "tool_name", nullable = false)
    private String toolName;

    @Column(name = "input_payload", columnDefinition = "json")
    private String inputPayload;

    @Column(name = "metadata", columnDefinition = "json")
    private String metadata;

    @OneToMany(mappedBy = "step")
    private Set<StepDependency> dependencies = new LinkedHashSet<>();

    @OneToMany(mappedBy = "dependsOnStep")
    private Set<StepDependency> dependents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "step")
    private Set<StepExecution> executions = new LinkedHashSet<>();

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public ExecutionPlan getPlan() {
        return plan;
    }

    public void setPlan(ExecutionPlan plan) {
        this.plan = plan;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getInputPayload() {
        return inputPayload;
    }

    public void setInputPayload(String inputPayload) {
        this.inputPayload = inputPayload;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Set<StepDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<StepDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public Set<StepDependency> getDependents() {
        return dependents;
    }

    public void setDependents(Set<StepDependency> dependents) {
        this.dependents = dependents;
    }

    public Set<StepExecution> getExecutions() {
        return executions;
    }

    public void setExecutions(Set<StepExecution> executions) {
        this.executions = executions;
    }
}
