package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StepDependencyId implements Serializable {

    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Column(name = "step_id", nullable = false)
    private String stepId;

    @Column(name = "depends_on_step_id", nullable = false)
    private String dependsOnStepId;

    public StepDependencyId() {
    }

    public StepDependencyId(String planId, String stepId, String dependsOnStepId) {
        this.planId = planId;
        this.stepId = stepId;
        this.dependsOnStepId = dependsOnStepId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getDependsOnStepId() {
        return dependsOnStepId;
    }

    public void setDependsOnStepId(String dependsOnStepId) {
        this.dependsOnStepId = dependsOnStepId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StepDependencyId that)) {
            return false;
        }
        return Objects.equals(planId, that.planId)
                && Objects.equals(stepId, that.stepId)
                && Objects.equals(dependsOnStepId, that.dependsOnStepId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId, stepId, dependsOnStepId);
    }
}
