# 04 – Model Strategy

## 1. Objectives

The model layer supports:

- Structured plan generation
- Deterministic execution
- Multi-provider flexibility
- Cost-aware reasoning
- Evaluation harness automation

---

## 2. LLM Abstraction

LLMs are abstracted behind a provider interface:

- OpenAI-compatible
- Ollama (local-first)
- Future providers

Configuration allows:

- Multiple models
- Default reasoning model
- Fallback models

Preference: locally running Ollama for development and cost control.

---

## 3. Planning Model

Planner model responsibilities:

- Convert natural language to structured ExecutionPlan
- Emit valid JSON
- Respect tool registry constraints

Planning prompt includes:

- Tool definitions
- Policy hints
- Cost constraints
- Execution rules

---

## 4. Model Configuration Strategy

Models configured via application configuration:

- Provider ID
- Model name
- Temperature
- Max tokens
- Cost metadata

This enables:

- Switching providers without code change
- Cost comparison experiments
- Model evaluation

---

## 5. Cost-Aware Planning (Future Phase)

Planner may receive:

- Estimated tool cost
- Token cost projections

Future enhancements:

- Model selection based on cost threshold
- Plan optimization via cheaper models

---

## 6. Evaluation Harness

Evaluation will:

- Store expected plan structures
- Compare generated plans
- Measure determinism
- Track cost over time

Metrics include:

- Plan validity rate
- Average token usage
- Execution latency
- Failure rate

---

## 7. Long-Term Model Roadmap

Phase 1:
- Single-step execution
- Structured output planning

Phase 2:
- Multi-step DAG planning
- Parallel execution

Phase 3:
- Cost-aware model routing
- Policy-aware plan refinement

Phase 4:
- Automated regression evaluation
- Benchmark dataset integration