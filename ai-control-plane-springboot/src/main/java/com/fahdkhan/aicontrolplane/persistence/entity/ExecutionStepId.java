package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExecutionStepId implements Serializable {

    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Column(name = "step_id", nullable = false)
    private String stepId;

    public ExecutionStepId() {
    }

    public ExecutionStepId(String planId, String stepId) {
        this.planId = planId;
        this.stepId = stepId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExecutionStepId that)) {
            return false;
        }
        return Objects.equals(planId, that.planId) && Objects.equals(stepId, that.stepId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId, stepId);
    }
}
