# 09 – Cost Analysis

## 1. Objectives

- Estimate cost **before execution**
- Track **actual cost** during execution
- Inform **policy enforcement**
- Enable **budget-aware planning**

---

## 2. Cost Components

| Component | Description |
|-----------|-------------|
| LLM Cost | Token usage * provider cost per 1k tokens |
| Tool Cost | Tool-specific estimated cost per invocation |
| Total Cost | Sum of LLM + tool costs |

---

## 3. Strategy

- Planner includes **cost metadata** in ExecutionPlan
- Execution engine updates **step_cost** and total_cost
- Policy layer can **reject plans exceeding budget**
- Observability layer logs cost for analysis and reporting

---

## 4. Example Usage

```json
{
  "planId": "inventory_report",
  "metadata": {
    "estimated_total_cost": 0.15
  },
  "steps": [
    {
      "stepId": "fetch_inventory",
      "metadata": { "estimated_cost": 0.05 }
    },
    {
      "stepId": "generate_report",
      "metadata": { "estimated_cost": 0.10 }
    }
  ]
}
```

Execution produces actual cost fields in StepExecution

Reports can compare estimated vs actual cost

