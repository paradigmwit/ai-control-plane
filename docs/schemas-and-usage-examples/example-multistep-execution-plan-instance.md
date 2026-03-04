## ExecutionPlan Schema

### This plan demonstrates a full multi-step enterprise workflow with:

- Parallel execution
- Dependent aggregation
- Deterministic tools + LLM for summarization
- Retry/fallback mechanisms
- Cost and policy metadata

---

### Json Schema
```
{
  "task_summary": "Generate consolidated inventory and sales report for SKU123, converted to EUR",
  "execution_steps": [
    {
      "step_id": "fetch_inventory",
      "tool": "erp_inventory_query",
      "input": { "sku": "SKU123" },
      "depends_on": [],
      "confidence": 0.95,
      "retry_policy": { "max_retries": 2, "backoff_seconds": 5 },
      "metadata": { "estimated_cost": 0.05, "permissions": ["inventory_team"] }
    },
    {
      "step_id": "fetch_sales",
      "tool": "crm_sales_query",
      "input": { "sku": "SKU123" },
      "depends_on": [],
      "confidence": 0.9,
      "retry_policy": { "max_retries": 2, "backoff_seconds": 5 },
      "metadata": { "estimated_cost": 0.05, "permissions": ["sales_team"] }
    },
    {
      "step_id": "currency_conversion",
      "tool": "currency_converter",
      "input": { "from_currency": "USD", "to_currency": "EUR" },
      "depends_on": [],
      "confidence": 0.95,
      "metadata": { "estimated_cost": 0.02, "permissions": ["finance_team"] }
    },
    {
      "step_id": "aggregate_data",
      "tool": "data_aggregator",
      "input": { "aggregation_type": "inventory_sales_summary" },
      "depends_on": ["fetch_inventory", "fetch_sales", "currency_conversion"],
      "confidence": 0.9
    },
    {
      "step_id": "generate_report",
      "tool": "llm_summarizer",
      "input": { "context": "aggregate_data" },
      "depends_on": ["aggregate_data"],
      "confidence": 0.92,
      "fallback": { "default_value": "Unable to generate report at this time." }
    }
  ],
  "metadata": {
    "estimated_cost": 0.20,
    "created_at": "2026-03-03T12:00:00Z",
    "created_by": "planner-llm-v1"
  }
}
```

---

## Explanation of Execution Flow

1. Parallel Independent Steps
    - `fetch_inventory`, `fetch_sales`, `currency_conversion`
    - Can execute concurrently since they have no dependencies

2. Dependent Step
    - `aggregate_data` waits for all three parallel steps
    - Aggregates inventory + sales + currency conversion

3. Final Step 
    - `generate_report` depends on `aggregate_data`
    - Uses LLM to summarize results
    - Has fallback in case LLM fails

4. Retries 
    - Each external API step (`fetch_inventory`, `fetch_sales`) has retry logic
    - Fallback used for LLM summarizer

5. Cost & Permissions 
    - Each step includes estimated cost and required permissions
    - Supports cost-aware execution and policy enforcement