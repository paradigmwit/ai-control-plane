package com.fahdkhan.aicontrolplane.persistence.repository;

import com.fahdkhan.aicontrolplane.persistence.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionPlanRepository extends JpaRepository<Plan, String> {
}
