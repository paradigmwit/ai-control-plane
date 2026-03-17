package com.fahdkhan.aicontrolplane.user;

import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionStep;
import com.fahdkhan.aicontrolplane.persistence.entity.Plan;
import com.fahdkhan.aicontrolplane.persistence.entity.StepDependency;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionPlanRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.ExecutionStepRepository;
import com.fahdkhan.aicontrolplane.persistence.repository.StepDependencyRepository;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConversionWorkflowSeedData {

    @Bean
    CommandLineRunner seedCurrencyConversionWorkflow(
            ExecutionPlanRepository planRepository,
            ExecutionStepRepository stepRepository,
            StepDependencyRepository dependencyRepository) {
        return _ -> {
            Plan plan = planRepository.findById(CurrencyConversionWorkflowService.PLAN_ID)
                    .orElseGet(() -> {
                        Plan seededPlan = new Plan();
                        seededPlan.setPlanId(CurrencyConversionWorkflowService.PLAN_ID);
                        seededPlan.setMetadata(planMetadataJson());
                        seededPlan.setCreatedAt(Instant.now());
                        return planRepository.save(seededPlan);
                    });

            upsertStep(stepRepository, plan,
                    CurrencyConversionWorkflowService.PARSE_STEP_ID,
                    "prompt_parser",
                    "{\"description\":\"extract amount + source/target currencies from prompt\"}");

            upsertStep(stepRepository, plan,
                    CurrencyConversionWorkflowService.RATE_STEP_ID,
                    "static_exchange_rate_lookup",
                    "{\"description\":\"resolve static exchange rates for supported currencies\"}");

            upsertStep(stepRepository, plan,
                    CurrencyConversionWorkflowService.CONVERT_STEP_ID,
                    "currency_calculator",
                    "{\"description\":\"multiply source amount by exchange rate\"}");

            if (dependencyRepository.findById("currency-conversion.depends.resolve-rate-on-parse").isEmpty()) {
                StepDependency dependency = new StepDependency();
                dependency.setId("currency-conversion.depends.resolve-rate-on-parse");
                dependency.setPlan(plan);
                dependency.setStep(stepRepository.getReferenceById(CurrencyConversionWorkflowService.RATE_STEP_ID));
                dependency.setDependsOnStep(stepRepository.getReferenceById(CurrencyConversionWorkflowService.PARSE_STEP_ID));
                dependencyRepository.save(dependency);
            }

            if (dependencyRepository.findById("currency-conversion.depends.convert-on-resolve-rate").isEmpty()) {
                StepDependency dependency = new StepDependency();
                dependency.setId("currency-conversion.depends.convert-on-resolve-rate");
                dependency.setPlan(plan);
                dependency.setStep(stepRepository.getReferenceById(CurrencyConversionWorkflowService.CONVERT_STEP_ID));
                dependency.setDependsOnStep(stepRepository.getReferenceById(CurrencyConversionWorkflowService.RATE_STEP_ID));
                dependencyRepository.save(dependency);
            }
        };
    }

    private void upsertStep(
            ExecutionStepRepository stepRepository,
            Plan plan,
            String stepId,
            String toolName,
            String inputPayloadJson) {
        ExecutionStep step = stepRepository.findById(stepId).orElseGet(ExecutionStep::new);
        step.setStepId(stepId);
        step.setPlan(plan);
        step.setToolName(toolName);
        step.setInputPayload(inputPayloadJson);
        step.setMetadata("{}");
        stepRepository.save(step);
    }

    private String planMetadataJson() {
        return "{\"task_summary\":\"Convert money from one currency to another\","
                + "\"workflow_type\":\"currency_conversion\","
                + "\"created_by\":\"application-startup\"}";
    }
}
