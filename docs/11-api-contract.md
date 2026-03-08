# 11 – API Contract

## Overview
This document defines the HTTP contract for the control-plane API surface.

## Error Envelope
Validation and argument failures return:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "timestamp": "2026-01-01T00:00:00Z",
  "errors": [
    {"field": "planId", "reason": "must not be blank"}
  ]
}
```

Codes:
- `VALIDATION_ERROR` → `400`
- `INVALID_ARGUMENT` → `400`
- `INTERNAL_ERROR` → `500`

## Resource Endpoints

### Execution Plans
- `GET /api/v1/execution-plans`
- `GET /api/v1/execution-plans/{planId}`
- `POST /api/v1/execution-plans`
- `PUT /api/v1/execution-plans/{planId}`
- `DELETE /api/v1/execution-plans/{planId}`

### Execution Steps (composite key: `planId + stepId`)
- `GET /api/v1/execution-plans/{planId}/steps`
- `GET /api/v1/execution-plans/{planId}/steps/{stepId}`
- `POST /api/v1/execution-plans/{planId}/steps`
- `PUT /api/v1/execution-plans/{planId}/steps/{stepId}`
- `DELETE /api/v1/execution-plans/{planId}/steps/{stepId}`

### Step Dependencies (composite key: `planId + stepId + dependsOnStepId`)
- `GET /api/v1/execution-plans/{planId}/step-dependencies`
- `GET /api/v1/execution-plans/{planId}/step-dependencies/{stepId}/depends-on/{dependsOnStepId}`
- `POST /api/v1/execution-plans/{planId}/step-dependencies`
- `PUT /api/v1/execution-plans/{planId}/step-dependencies/{stepId}/depends-on/{dependsOnStepId}`
- `DELETE /api/v1/execution-plans/{planId}/step-dependencies/{stepId}/depends-on/{dependsOnStepId}`

### Execution Instances
- `GET /api/v1/execution-instances`
- `GET /api/v1/execution-instances/{executionId}`
- `POST /api/v1/execution-instances`
- `PUT /api/v1/execution-instances/{executionId}`
- `DELETE /api/v1/execution-instances/{executionId}`

### Step Executions
- `GET /api/v1/executions/{executionId}/steps`
- `GET /api/v1/executions/{executionId}/steps/{stepId}`
- `POST /api/v1/executions/{executionId}/steps`
- `PUT /api/v1/executions/{executionId}/steps/{stepId}`
- `DELETE /api/v1/executions/{executionId}/steps/{stepId}`

### LLM Metadata
- `GET /api/v1/llm-metadata`
- `GET /api/v1/llm-metadata/{executionId}`
- `POST /api/v1/llm-metadata`
- `PUT /api/v1/llm-metadata/{executionId}`
- `DELETE /api/v1/llm-metadata/{executionId}`

## Orchestration Commands
- `POST /api/v1/planning/plan`
- `POST /api/v1/executions/{executionId}:start`
- `POST /api/v1/executions/{executionId}:cancel`
- `POST /api/v1/executions/{executionId}:retry-failed`
- `GET /api/v1/executions/{executionId}/timeline`

## Execution Status Transitions
- `CREATED -> VALIDATED -> POLICY_APPROVED -> RUNNING -> COMPLETED`
- `RUNNING -> FAILED`
- `RUNNING -> CANCELLED`
- `FAILED -> RUNNING` (retry-failed)
