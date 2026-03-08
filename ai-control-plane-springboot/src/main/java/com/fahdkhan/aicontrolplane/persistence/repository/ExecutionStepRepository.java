package com.fahdkhan.aicontrolplane.persistence.repository;

import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionStep;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionStepRepository extends JpaRepository<ExecutionStep, String> {

    List<ExecutionStep> findByPlanPlanId(String planId);
}
