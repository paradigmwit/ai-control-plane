package com.fahdkhan.aicontrolplane.model;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public record ExecutionPlan(

        @NonNull
        String planId,

        @NonNull
        List<ExecutionStep> steps,

        Map<String, Object> metadata

) {}