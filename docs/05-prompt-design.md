# 05 – Prompt Design

## 1. Objectives

The Planner uses LLMs to generate **structured execution plans**. Prompt design is critical to:

- Ensure valid JSON output
- Enforce tool and policy constraints
- Include cost-awareness hints
- Reduce hallucinations

---

## 2. Prompt Structure

### Sections:

1. **Context:** Tool definitions, policies, cost per tool/LLM, global variables.
2. **Task:** User natural language request.
3. **Constraints:** Required fields, DAG validity, step dependencies.
4. **Output Format:** JSON schema for `ExecutionPlan`.

---

## 3. Example Prompt

```text
Task: Generate a plan to summarize inventory data and create a sales report.
Constraints:
- All steps must have a unique step_id.
- DAG must be acyclic.
- Use only approved tools.
- Output must follow JSON schema: ExecutionPlan {planId, steps, metadata}.
Include estimated cost in metadata
```

___

## 4. Best Practices

Always validate LLM output against schema

Use few-shot examples for complex workflows

Include tool descriptions in the prompt

Minimize ambiguity to prevent invalid JSON

Log raw prompt and response for observability