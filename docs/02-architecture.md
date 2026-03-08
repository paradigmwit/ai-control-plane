# 02 – Architecture

## 1. System Overview

AI Control Plane is a Spring Boot–based orchestration system responsible for:

- Translating user intent into structured execution plans
- Validating and governing plans
- Executing DAG-based workflows
- Managing tool invocation
- Tracking cost and metadata
- Enabling replay and audit

It is architected as a **control-plane**, not a direct inference application.

---

## 2. Architectural Principles

### Separation of Concerns

- Planning (LLM reasoning)
- Validation (DAG + schema validation)
- Policy enforcement (governance)
- Execution orchestration
- Persistence
- Observability

Each layer is isolated and independently testable.

---

## 3. High-Level Components

### API Layer
- Accepts user requests
- Triggers planning and execution
- Returns execution status

### Planner
- Uses configured LLM providers
- Generates structured execution plans
- Returns immutable plan definitions

### Validator
- Ensures plan integrity
- Detects DAG cycles
- Validates tool existence
- Enforces structural constraints

### Policy Engine
- Evaluates plan against governance rules
- Applies budget constraints
- Rejects unsafe or high-risk actions

### Execution Engine
- Creates execution instances
- Schedules ready steps
- Supports parallel execution
- Updates runtime state

### Tool Registry
- Maps tool names to implementations
- Supports pluggable tool model

### Persistence Layer
- Stores plan definitions
- Stores execution state
- Supports replay

---

## 4. Execution Model

Execution follows this lifecycle:

1. User submits intent
2. Planner generates ExecutionPlan
3. Plan is validated
4. Policy checks are applied
5. ExecutionInstance is created
6. Steps are scheduled based on DAG dependencies
7. Steps execute (possibly in parallel)
8. Execution completes
9. Metadata and cost are recorded

---

## 5. Non-Functional Considerations

### Concurrency
- Step execution supports parallel scheduling
- Status transitions are atomic

### Replayability
- ExecutionInstance captures full runtime state
- Plans are immutable once persisted

### Observability
- Cost tracking
- Step duration metrics
- Full LLM raw responses retained

### Extensibility
- Multiple LLM providers
- New tools via registry
- Policy plug-ins

---

## 6. Deployment Model

Current target:
- Monolithic Spring Boot application
- Embedded H2 (dev)
- PostgreSQL (prod-ready)

Future evolution:
- Distributed execution workers
- Externalized scheduler
- Event-driven step execution