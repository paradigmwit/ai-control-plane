## ExecutionPlan Schema

### This schema now allows:

- Planner output validation (ADR-002)
- Execution engine DAG resolution (ADR-005)
- Policy enforcement using step metadata (ADR-003 & ADR-006)
- Retries, fallbacks, and observability logging (ADR-008 & ADR-009)
- Cost-aware execution tracking (ADR-007)

---

### Json Schema
```
{
  "task_summary": "Generate quarterly financial report for Q1",
  "execution_steps": [
    {
      "step_id": "fetch_revenue",
      "tool": "sql_query",
      "input": { "query": "SELECT revenue FROM finance_q1" },
      "depends_on": [],
      "confidence": 0.95,
      "retry_policy": { "max_retries": 2, "backoff_seconds": 5 }
    },
    {
      "step_id": "calculate_growth",
      "tool": "growth_calculator",
      "input": {},
      "depends_on": ["fetch_revenue"],
      "confidence": 0.9
    },
    {
      "step_id": "summarize_report",
      "tool": "llm_summarizer",
      "input": { "context": "growth_analysis" },
      "depends_on": ["calculate_growth"],
      "confidence": 0.92,
      "fallback": { "default_value": "Report could not be generated" }
    }
  ],
  "metadata": {
    "estimated_cost": 0.25,
    "created_at": "2026-03-03T10:00:00Z",
    "created_by": "planner-llm-v1"
  }
}
```