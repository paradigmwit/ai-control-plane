## ExecutionPlan Schema

### Key Notes

- `task_summary`: human-readable high-level description.
- `execution_steps`: array of structured steps forming a DAG.
- `step_id`: unique identifier for dependency resolution.
- `tool`: deterministic tool name from Tool Registry.
- `depends_on`: enforces DAG execution order.
- `confidence`: optional LLM confidence score for reasoning trace.
- `retry_policy` & `fallback`: implement ADR-008 retry/fallback logic.
- `metadata`: cost estimates, permissions, or other auxiliary data.

---

### Json Schema
```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "ExecutionPlan",
  "type": "object",
  "required": ["task_summary", "execution_steps"],
  "properties": {
    "task_summary": {
      "type": "string",
      "description": "High-level summary of the workflow or user request."
    },
    "execution_steps": {
      "type": "array",
      "minItems": 1,
      "items": { "$ref": "#/definitions/ExecutionStep" }
    },
    "metadata": {
      "type": "object",
      "description": "Optional plan-level metadata (e.g., cost estimate, context)",
      "properties": {
        "estimated_cost": { "type": "number" },
        "created_at": { "type": "string", "format": "date-time" },
        "created_by": { "type": "string" }
      }
    }
  },
  "definitions": {
    "ExecutionStep": {
      "type": "object",
      "required": ["step_id", "tool", "input", "depends_on"],
      "properties": {
        "step_id": {
          "type": "string",
          "description": "Unique identifier for the step."
        },
        "tool": {
          "type": "string",
          "description": "Name of the tool to execute."
        },
        "input": {
          "type": "object",
          "description": "Input parameters for the tool."
        },
        "depends_on": {
          "type": "array",
          "items": { "type": "string" },
          "description": "List of step_ids this step depends on."
        },
        "confidence": {
          "type": "number",
          "minimum": 0,
          "maximum": 1,
          "description": "Planner confidence score for this step."
        },
        "retry_policy": {
          "type": "object",
          "properties": {
            "max_retries": { "type": "integer", "minimum": 0 },
            "backoff_seconds": { "type": "number", "minimum": 0 }
          }
        },
        "fallback": {
          "type": "object",
          "description": "Optional fallback tool or default value if step fails.",
          "properties": {
            "tool": { "type": "string" },
            "default_value": {}
          }
        },
        "metadata": {
          "type": "object",
          "description": "Optional per-step metadata (cost, permissions, etc.)",
          "properties": {
            "estimated_cost": { "type": "number" },
            "permissions": { "type": "array", "items": { "type": "string" } }
          }
        }
      }
    }
  }
}```