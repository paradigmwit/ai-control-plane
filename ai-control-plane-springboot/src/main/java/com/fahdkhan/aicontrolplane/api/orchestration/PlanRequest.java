package com.fahdkhan.aicontrolplane.api.orchestration;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record PlanRequest(@NotBlank String intent, String metadata, @DecimalMin("0.0") BigDecimal budgetLimit) {
}
