package com.fahdkhan.aicontrolplane.orchestration;

import com.fahdkhan.aicontrolplane.api.orchestration.ExecutionTimelineResponse;
import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionInstance;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionInstanceRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.StepExecutionRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExecutionCommandService {

    private final ExecutionInstanceRepository executionInstanceRepository;
    private final StepExecutionRepository stepExecutionRepository;

    public ExecutionCommandService(
            ExecutionInstanceRepository executionInstanceRepository,
            StepExecutionRepository stepExecutionRepository) {
        this.executionInstanceRepository = executionInstanceRepository;
        this.stepExecutionRepository = stepExecutionRepository;
    }

    public ExecutionInstance start(String executionId) {
        ExecutionInstance execution = executionInstanceRepository.getReferenceById(executionId);
        execution.setStatus(ExecutionStatus.RUNNING);
        if (execution.getStartedAt() == null) {
            execution.setStartedAt(Instant.now());
        }
        return executionInstanceRepository.save(execution);
    }

    public ExecutionInstance cancel(String executionId) {
        ExecutionInstance execution = executionInstanceRepository.getReferenceById(executionId);
        execution.setStatus(ExecutionStatus.CANCELLED);
        execution.setCompletedAt(Instant.now());
        return executionInstanceRepository.save(execution);
    }

    public ExecutionInstance retryFailed(String executionId) {
        ExecutionInstance execution = executionInstanceRepository.getReferenceById(executionId);
        execution.setStatus(ExecutionStatus.RUNNING);
        execution.setCompletedAt(null);
        return executionInstanceRepository.save(execution);
    }

    public ExecutionTimelineResponse timeline(String executionId) {
        ExecutionInstance execution = executionInstanceRepository.getReferenceById(executionId);
        List<StepExecutionDto> steps = stepExecutionRepository.findByExecutionExecutionId(executionId).stream()
                .map(step -> new StepExecutionDto(
                        step.getId().getExecutionId(),
                        step.getPlanId(),
                        step.getId().getStepId(),
                        step.getStatus().name(),
                        step.getOutputPayload(),
                        step.getErrorMessage(),
                        step.getStartedAt(),
                        step.getCompletedAt(),
                        step.getExecutionTimeMs(),
                        step.getStepCost()))
                .sorted(Comparator.comparing(StepExecutionDto::stepId))
                .toList();
        return new ExecutionTimelineResponse(executionId, execution.getStatus().name(), steps);
    }
}
