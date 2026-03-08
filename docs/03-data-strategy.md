# 03 – Data Strategy

## 1. Data Philosophy

The system distinguishes between:

- Definition data (ExecutionPlan)
- Runtime data (ExecutionInstance, StepExecution)
- Observability data (LLM metadata, cost)

Plans are immutable. Runtime state is mutable.

---

## 2. Schema Design

Schema: `control_plane`

Key tables:

- execution_plan
- execution_step
- step_dependency
- execution_instance
- step_execution
- llm_metadata

The schema enforces:

- Plan-to-step relationships
- Execution-to-plan linkage
- Execution-to-step linkage

DAG correctness is validated at application level.

---

## 3. JSON Strategy

Tool inputs and outputs are stored as JSONB.

Rationale:

- Tool contracts evolve
- Avoid frequent schema migrations
- Maintain flexibility

Tradeoff:

- Reduced relational enforcement
- Requires application-level validation

---

## 4. Versioning Strategy

Plans can support versioning via:

- plan_id version suffix
- created_at metadata
- future parent_plan_id linkage

Executions reference a specific immutable plan snapshot.

---

## 5. Cost and Observability Data

Cost tracking fields:

- step_cost
- llm_cost
- total_cost

LLM metadata includes:

- Provider
- Model
- Token usage
- Raw response

This enables:

- Cost-aware planning
- Budget enforcement
- Evaluation harness comparison

---

## 6. Integrity Strategy

Relational integrity enforces:

- Plan existence
- Execution existence

DAG integrity enforced by:

- Validator layer
- Cycle detection
- Tool existence checks

---

## 7. Replay Strategy

Replay uses:

- Stored plan definition
- ExecutionInstance snapshot
- StepExecution records

Execution can be:

- Re-run fully
- Re-run from failed step
- Simulated for evaluation