# Overview

## Purpose

ai-control-plane is a governed AI orchestration framework that demonstrates how large language models (LLMs) can be integrated into production systems using structured planning, deterministic execution, and policy enforcement.

The project focuses on building a control layer that:

- Converts natural language requests into structured execution plans
- Invokes deterministic tools in a controlled manner
- Enforces governance and cost constraints
- Produces replayable and auditable execution traces

This is not a chatbot application.  
It is an execution control plane for AI-driven workflows.

---

## Core Idea

Most AI applications tightly couple reasoning and execution.

ai-control-plane deliberately separates:

- **Reasoning (LLM Planner)**
- **Execution (DAG Engine)**
- **Governance (Policy Layer)**
- **Observability (Telemetry + Replay)**

This separation enables:

- Deterministic execution
- Reduced hallucination risk
- Cost control
- Traceability
- Incremental improvement

---

## Practical Enterprise Use Cases

These examples illustrate workflows where `ai-control-plane` adds value in real-world scenarios.

---

### 1. Multi-Step Financial Workflow

**User Request:**
> "Convert 500,000 USD to EUR, calculate 19% VAT, apply quarterly tax adjustment, and summarize the total."

**Execution Steps:**
1. Currency conversion API/tool
2. Deterministic VAT calculation
3. Tax adjustment calculation using business rules
4. Professional summary generation via LLM

**Demonstrates:**
- Multi-step planning
- Deterministic computation
- Integration with external and internal tools
- LLM used only for reasoning and summarization
- Traceable calculation logs

---

### 2. Parallel Data Aggregation Across Services

**User Request:**
> "Fetch current inventory for product SKU123 from ERP and sales data from CRM, then summarize stock levels and sales performance."

**Execution Steps:**
1. ERP database query (inventory)
2. CRM API query (sales)
3. Parallel execution of steps 1 and 2
4. LLM synthesizes summary for management

**Demonstrates:**
- Parallel execution
- Dependency graph resolution
- Safe external API integration
- Traceable workflow for audit purposes

---

### 3. Analytics & Reporting Workflow

**User Request:**
> "Retrieve Q1 revenue data, calculate YoY growth, detect anomalies, and generate a management report."

**Execution Steps:**
1. SQL query tool to pull revenue
2. Deterministic growth calculation
3. Statistical anomaly detection
4. LLM-based report summarization with charts

**Demonstrates:**
- Data + computation hybrid workflow
- Use of deterministic tools for numeric operations
- Controlled AI reasoning for natural language output
- Integration of evaluation metrics

---

### 4. Controlled Operational Workflow

**User Request:**
> "Export sensitive customer records and send via email."

**Execution Steps:**
1. Policy engine checks permissions
2. Execution blocked due to security policy
3. LLM generates explanation to user

**Demonstrates:**
- Governance layer
- Safety controls
- AI used for user communication without executing sensitive actions

---

### 5. Cost-Aware Workflow Planning

**User Request:**
> "Run multiple API-heavy analytics for 50 products and summarize results."

**Execution Steps:**
1. Planner estimates token and API costs
2. Policy engine evaluates cost limits
3. Steps are pruned or batched to remain within thresholds
4. LLM generates summary of results

**Demonstrates:**
- Integration of cost governance
- Proactive planning and throttling
- Evaluation of trade-offs in execution

---

### 6. Failure Recovery Workflow

**User Request:**
> "Fetch financial data from ERP and CRM, then generate combined report."

**Execution Steps:**
1. ERP API call fails (timeout)
2. Execution engine retries or falls back to cached data
3. CRM API call succeeds
4. LLM generates partial report highlighting fallback

**Demonstrates:**
- Robust failure handling
- Retry policies
- Safe propagation of partial results
- Observability and logging for debugging

---

### Key Takeaways

1. AI reasoning is **never directly executing business logic**.
2. Deterministic tools handle numeric calculations, queries, and API interactions.
3. LLM is leveraged for **planning, summarization, and explanation**.
4. Policy and cost controls ensure **safe, auditable, and predictable execution**.
5. Parallelism, DAG resolution, and fallback mechanisms reflect **production-grade workflow orchestration**.

These examples can be extended to any domain while preserving the same architectural rigor.

---

## Why This Matters

Enterprises adopting AI face recurring challenges:

- Tool hallucination
- Uncontrolled execution
- Lack of auditability
- Cost unpredictability
- Difficulty debugging LLM behavior

ai-control-plane addresses these issues by treating AI as a component inside a governed system rather than an autonomous agent.

---

## Project Scope

This repository includes:

- Implementation of the orchestration engine
- Lifecycle documentation
- Evaluation framework
- Cost analysis
- Architecture decision records (ADRs)
- Example workflows

The goal is to demonstrate AI integration discipline across the full engineering lifecycle.