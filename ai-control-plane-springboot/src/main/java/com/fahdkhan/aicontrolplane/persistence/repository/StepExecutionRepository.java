package com.fahdkhan.aicontrolplane.persistence.repository;

import com.fahdkhan.aicontrolplane.persistence.entity.StepExecution;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepExecutionRepository extends JpaRepository<StepExecution, String> {

    List<StepExecution> findByStepExecutionId(String stepExecutionId);

    List<StepExecution> findByInstanceInstanceId(String instanceId);
}
