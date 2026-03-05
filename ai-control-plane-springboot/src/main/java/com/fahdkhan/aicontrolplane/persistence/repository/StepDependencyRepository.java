package com.fahdkhan.aicontrolplane.persistence.repository;

import com.fahdkhan.aicontrolplane.persistence.entity.StepDependency;
import com.fahdkhan.aicontrolplane.persistence.entity.StepDependencyId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepDependencyRepository extends JpaRepository<StepDependency, StepDependencyId> {

    List<StepDependency> findByStepStepId(String stepId);
}
