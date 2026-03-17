package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "plan", schema = "control_plane")
@Getter
@Setter
public class Plan {

    @Id
    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Column(name = "metadata", columnDefinition = "json")
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "plan")
    private Set<Instance> executions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "plan")
    private Set<ExecutionStep> steps = new LinkedHashSet<>();

    @OneToMany(mappedBy = "plan")
    private Set<StepDependency> dependsOnStep = new LinkedHashSet<>();
}
