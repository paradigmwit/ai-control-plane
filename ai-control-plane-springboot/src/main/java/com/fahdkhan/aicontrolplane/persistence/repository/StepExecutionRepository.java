package com.fahdkhan.aicontrolplane.persistence.repository;

import com.fahdkhan.aicontrolplane.persistence.entity.StepExecution;
import com.fahdkhan.aicontrolplane.persistence.entity.StepExecutionId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepExecutionRepository extends JpaRepository<StepExecution, StepExecutionId> {

    List<StepExecution> findByExecutionExecutionId(String executionId);
}
