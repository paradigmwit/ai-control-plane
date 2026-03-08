package com.fahdkhan.aicontrolplane.persistence.repository;

import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionPlanRepository extends JpaRepository<ExecutionPlan, String> {
}
