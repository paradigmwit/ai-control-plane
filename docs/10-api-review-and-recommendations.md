# 10 – API Review and Improvement Recommendations

## Context Used
This review aligns with:
- lifecycle and orchestration constraints in `02-architecture.md`
- replay/observability requirements in `08-observability.md`
- execution DAG requirements in `workflows/*.md`

## Implemented Improvements

### 1. API layer for orchestration lifecycle
Added resource APIs and orchestration command APIs so the control-plane can be used beyond health checks.

### 2. Composite-key alignment
- `ExecutionStep` now uses explicit composite key `(plan_id, step_id)` in JPA.
- `StepDependency` now includes `planId` in identifier and API contract.

### 3. Validation and standardized error responses
- Added bean validation on DTOs.
- Added global exception handler with a consistent JSON error envelope.

### 4. Orchestration command endpoints
Added:
- `POST /api/v1/planning/plan`
- `POST /api/v1/executions/{id}:start`
- `POST /api/v1/executions/{id}:cancel`
- `POST /api/v1/executions/{id}:retry-failed`
- `GET /api/v1/executions/{id}/timeline`

### 5. API contract documentation
Added `docs/11-api-contract.md` with endpoint list, error schema, and status transitions.
