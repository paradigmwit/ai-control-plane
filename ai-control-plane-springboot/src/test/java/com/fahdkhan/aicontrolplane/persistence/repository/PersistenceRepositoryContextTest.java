package com.fahdkhan.aicontrolplane.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PersistenceRepositoryContextTest {

    @Autowired
    private ExecutionPlanRepository executionPlanRepository;

    @Autowired
    private ExecutionStepRepository executionStepRepository;

    @Autowired
    private StepDependencyRepository stepDependencyRepository;

    @Autowired
    private ExecutionInstanceRepository executionInstanceRepository;

    @Autowired
    private StepExecutionRepository stepExecutionRepository;

    @Autowired
    private LlmMetadataRepository llmMetadataRepository;

    @Test
    void shouldLoadAllRepositories() {
        assertNotNull(executionPlanRepository);
        assertNotNull(executionStepRepository);
        assertNotNull(stepDependencyRepository);
        assertNotNull(executionInstanceRepository);
        assertNotNull(stepExecutionRepository);
        assertNotNull(llmMetadataRepository);
    }
}
