package com.fahdkhan.aicontrolplane.execution;

public record CostBreakdown(
        double llmCost,
        double toolCost,
        double totalCost
) {

}