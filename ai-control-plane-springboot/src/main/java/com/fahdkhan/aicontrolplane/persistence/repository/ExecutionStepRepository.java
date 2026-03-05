package com.fahdkhan.aicontrolplane.persistence.repository;

import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionStep;
import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionStepId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionStepRepository extends JpaRepository<ExecutionStep, ExecutionStepId> {

    List<ExecutionStep> findByPlanPlanId(String planId);
}
