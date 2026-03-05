package com.fahdkhan.aicontrolplane.model;


import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public record ExecutionStep(

        @NonNull
        String stepId,

        @NonNull
        String toolName,

        Map<String, Object> input,

        List<String> dependsOn,

        Map<String, Object> metadata

) {}