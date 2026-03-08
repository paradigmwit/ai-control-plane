# 08 – Observability

## 1. Objectives

- Monitor **execution state** and **plan outcomes**
- Track **step-level metrics**
- Track **LLM and tool usage**
- Enable **replay and audit**

---

## 2. Data Captured

| Category | Captured Data |
|----------|---------------|
| Execution | execution_id, plan_id, status, timestamps |
| Step Execution | step_id, status, output, errors, duration, cost |
| LLM | provider_id, model, token usage, raw response |
| Metrics | average latency, success/failure rate, cost per run |

---

## 3. Implementation

- Structured logging (JSON)
- Micrometer metrics + Spring Boot Actuator
- Correlation IDs per execution
- Persist all ExecutionInstance and StepExecution data for replay
- Alerts on failed executions or high-cost runs