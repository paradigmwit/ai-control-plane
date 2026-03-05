package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StepExecutionId implements Serializable {

    @Column(name = "execution_id", nullable = false)
    private String executionId;

    @Column(name = "step_id", nullable = false)
    private String stepId;

    public StepExecutionId() {
    }

    public StepExecutionId(String executionId, String stepId) {
        this.executionId = executionId;
        this.stepId = stepId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
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
        if (!(o instanceof StepExecutionId that)) {
            return false;
        }
        return Objects.equals(executionId, that.executionId)
                && Objects.equals(stepId, that.stepId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executionId, stepId);
    }
}
