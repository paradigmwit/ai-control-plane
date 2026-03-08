# 10 – API Review and Improvement Recommendations

## Context Used
This review aligns with:
- lifecycle and orchestration constraints in `02-architecture.md`
- replay/observability requirements in `08-observability.md`
- execution DAG requirements in `workflows/*.md`

## Code Review Findings

### 1. API layer gap was blocking end-to-end orchestration
The architecture expects an API boundary for submission, tracking, and replay, but the code only exposed a health endpoint. CRUD REST resources have now been added for:
- execution plans
- execution steps
- step dependencies
- execution instances
- step executions
- LLM metadata

### 2. Composite-key modeling should be aligned between schema and JPA
`execution_step` uses `(plan_id, step_id)` as the primary key in schema, while service lookup currently addresses a single `stepId`. This works only if step IDs are globally unique in practice. Recommended follow-up:
- move to explicit composite IDs for `ExecutionStep`
- include `planId` in step dependency identifiers at API/service layer for strict relational correctness

### 3. Add input validation + error contract
Current DTOs are unvalidated pass-through records. Recommended follow-up:
- bean validation annotations (`@NotBlank`, enums, cost/time bounds)
- standardized error envelope for `400/404/409`
- conversion of invalid enum parsing into friendly client errors

### 4. Add orchestration endpoints beyond persistence CRUD
To match the control-plane lifecycle in the architecture docs, add higher-level command endpoints:
- `POST /api/v1/planning/plan` (intent -> validated plan)
- `POST /api/v1/executions/{id}:start`
- `POST /api/v1/executions/{id}:cancel`
- `POST /api/v1/executions/{id}:retry-failed`
- `GET /api/v1/executions/{id}/timeline` (replay/trace view)

### 5. Add API contract documentation
Recommended follow-up:
- OpenAPI generation and examples per workflow scenario
- document status transitions and terminal-state semantics

## New REST Endpoints Added

### Plans
- `GET /api/v1/execution-plans`
- `GET /api/v1/execution-plans/{planId}`
- `POST /api/v1/execution-plans`
- `PUT /api/v1/execution-plans/{planId}`
- `DELETE /api/v1/execution-plans/{planId}`

### Steps
- `GET /api/v1/execution-steps`
- `GET /api/v1/execution-steps/{stepId}`
- `POST /api/v1/execution-steps`
- `PUT /api/v1/execution-steps/{stepId}`
- `DELETE /api/v1/execution-steps/{stepId}`

### Step Dependencies
- `GET /api/v1/step-dependencies`
- `GET /api/v1/step-dependencies/{stepId}/depends-on/{dependsOnStepId}`
- `POST /api/v1/step-dependencies`
- `PUT /api/v1/step-dependencies/{stepId}/depends-on/{dependsOnStepId}`
- `DELETE /api/v1/step-dependencies/{stepId}/depends-on/{dependsOnStepId}`

### Execution Instances
- `GET /api/v1/execution-instances`
- `GET /api/v1/execution-instances/{executionId}`
- `POST /api/v1/execution-instances`
- `PUT /api/v1/execution-instances/{executionId}`
- `DELETE /api/v1/execution-instances/{executionId}`

### Step Executions
- `GET /api/v1/step-executions`
- `GET /api/v1/step-executions/{executionId}/steps/{stepId}`
- `POST /api/v1/step-executions`
- `PUT /api/v1/step-executions/{executionId}/steps/{stepId}`
- `DELETE /api/v1/step-executions/{executionId}/steps/{stepId}`

### LLM Metadata
- `GET /api/v1/llm-metadata`
- `GET /api/v1/llm-metadata/{executionId}`
- `POST /api/v1/llm-metadata`
- `PUT /api/v1/llm-metadata/{executionId}`
- `DELETE /api/v1/llm-metadata/{executionId}`
