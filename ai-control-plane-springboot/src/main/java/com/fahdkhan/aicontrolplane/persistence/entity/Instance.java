package com.fahdkhan.aicontrolplane.persistence.entity;

import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "execution_instance", schema = "control_plane")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instance {

    @Id
    @Column(name = "instance_id", nullable = false)
    private String instanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "status", nullable = false)
    private ExecutionStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "total_cost", precision = 19, scale = 4)
    private BigDecimal totalCost;

    @OneToMany(mappedBy = "instance")
    private Set<StepExecution> stepExecutions = new LinkedHashSet<>();

    @OneToOne(mappedBy = "instance")
    private LlmMetadata llmMetadata;
}
