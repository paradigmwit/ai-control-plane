package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "step_dependency")
public class StepDependency {

    @EmbeddedId
    private StepDependencyId id;

    @MapsId("planId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private ExecutionPlan plan;

    @MapsId("stepId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "step_id", referencedColumnName = "step_id", nullable = false)
    })
    private ExecutionStep step;

    @MapsId("dependsOnStepId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "depends_on_step_id", referencedColumnName = "step_id", nullable = false)
    })
    private ExecutionStep dependsOnStep;

    public StepDependencyId getId() {
        return id;
    }

    public void setId(StepDependencyId id) {
        this.id = id;
    }

    public ExecutionPlan getPlan() {
        return plan;
    }

    public void setPlan(ExecutionPlan plan) {
        this.plan = plan;
    }

    public ExecutionStep getStep() {
        return step;
    }

    public void setStep(ExecutionStep step) {
        this.step = step;
    }

    public ExecutionStep getDependsOnStep() {
        return dependsOnStep;
    }

    public void setDependsOnStep(ExecutionStep dependsOnStep) {
        this.dependsOnStep = dependsOnStep;
    }
}
