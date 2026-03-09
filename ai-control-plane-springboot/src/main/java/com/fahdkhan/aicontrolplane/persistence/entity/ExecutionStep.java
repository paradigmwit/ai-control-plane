package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "execution_step", schema = "control_plane")
@Getter
@Setter
public class ExecutionStep {

    @Id
    @Column(name = "step_id", nullable = false)
    private String stepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

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
}
