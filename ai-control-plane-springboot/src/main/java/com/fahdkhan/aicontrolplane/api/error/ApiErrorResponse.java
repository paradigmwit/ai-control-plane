package com.fahdkhan.aicontrolplane.api.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(String code, String message, Instant timestamp, List<ApiFieldError> errors) {
}
