package com.fahdkhan.aicontrolplane.persistence.entity;

import com.fahdkhan.aicontrolplane.model.StepStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "step_execution", schema = "control_plane")
@Getter
@Setter
public class StepExecution {

    @Id
    @Column(name = "step_execution_id", nullable = false)
    String stepExecutionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", nullable = false)
    private Instance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private ExecutionStep step;

    @Column(name = "status", nullable = false)
    private StepStatus status;

    @Column(name = "output_payload", columnDefinition = "json")
    private String outputPayload;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "step_cost", precision = 19, scale = 4)
    private BigDecimal stepCost;

}
