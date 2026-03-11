package com.fahdkhan.aicontrolplane.persistence.repository;

import com.fahdkhan.aicontrolplane.persistence.entity.StepDependency;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepDependencyRepository extends JpaRepository<StepDependency, String> {

    List<StepDependency> findByStepStepId(String stepId);
}
