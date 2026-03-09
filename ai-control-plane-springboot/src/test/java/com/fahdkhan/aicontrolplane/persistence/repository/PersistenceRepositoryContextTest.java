package com.fahdkhan.aicontrolplane.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class PersistenceRepositoryContextTest {

    @Autowired
    private ExecutionPlanRepository executionPlanRepository;

    @Autowired
    private ExecutionStepRepository executionStepRepository;

    @Autowired
    private StepDependencyRepository stepDependencyRepository;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private StepExecutionRepository stepExecutionRepository;

    @Autowired
    private LlmMetadataRepository llmMetadataRepository;

    @Test
    void shouldLoadAllRepositories() {
        assertNotNull(executionPlanRepository);
        assertNotNull(executionStepRepository);
        assertNotNull(stepDependencyRepository);
        assertNotNull(instanceRepository);
        assertNotNull(stepExecutionRepository);
        assertNotNull(llmMetadataRepository);
    }
}
