package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "step_dependency", schema = "control_plane")
public class StepDependency {

    @EmbeddedId
    private StepDependencyId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", insertable = false, updatable = false),
        @JoinColumn(name = "step_id", referencedColumnName = "step_id", insertable = false, updatable = false)
    })
    private ExecutionStep step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", insertable = false, updatable = false),
        @JoinColumn(name = "depends_on_step_id", referencedColumnName = "step_id", insertable = false, updatable = false)
    })
    private ExecutionStep dependsOnStep;

    public StepDependencyId getId() {
        return id;
    }

    public void setId(StepDependencyId id) {
        this.id = id;
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
