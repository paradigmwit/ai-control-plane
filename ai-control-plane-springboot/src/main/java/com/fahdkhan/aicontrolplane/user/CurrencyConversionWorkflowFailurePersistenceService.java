package com.fahdkhan.aicontrolplane.user;

import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.model.StepStatus;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionInstanceDto;
import com.fahdkhan.aicontrolplane.persistence.dto.StepExecutionDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionInstanceService;
import com.fahdkhan.aicontrolplane.persistence.service.StepExecutionService;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrencyConversionWorkflowFailurePersistenceService {

    private final ExecutionInstanceService executionInstanceService;
    private final StepExecutionService stepExecutionService;

    public CurrencyConversionWorkflowFailurePersistenceService(
            ExecutionInstanceService executionInstanceService,
            StepExecutionService stepExecutionService) {
        this.executionInstanceService = executionInstanceService;
        this.stepExecutionService = stepExecutionService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistFailure(
            String instanceId,
            String stepExecutionId,
            String stepId,
            Instant createdAt,
            Instant startedAt,
            Instant failedAt,
            String errorPayload) {
        executionInstanceService.save(new ExecutionInstanceDto(
                instanceId,
                CurrencyConversionWorkflowService.PLAN_ID,
                ExecutionStatus.FAILED.name(),
                createdAt,
                startedAt,
                failedAt,
                BigDecimal.ZERO,
                errorPayload));

        if (stepExecutionId != null && stepId != null) {
            stepExecutionService.save(new StepExecutionDto(
                    stepExecutionId,
                    instanceId,
                    stepId,
                    StepStatus.FAILED.name(),
                    null,
                    errorPayload,
                    startedAt,
                    failedAt,
                    Math.max(0L, failedAt.toEpochMilli() - startedAt.toEpochMilli()),
                    BigDecimal.ZERO));
        }
    }
}
