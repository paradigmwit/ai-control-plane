package com.fahdkhan.aicontrolplane.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "step_dependency", schema = "control_plane")
@Getter
@Setter
public class StepDependency {

    @Id
    @Column(name = "step_dependency_id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private ExecutionStep step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depends_on_step_id", nullable = false)
    private ExecutionStep dependsOnStep;
}
