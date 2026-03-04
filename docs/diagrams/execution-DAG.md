```mermaid
flowchart TD

%% User request triggers DAG generation
A[User Request: Multi-Step Workflow] --> B[Planner generates Execution Plan DAG]

    %% DAG steps
    B --> C1[Step 1: Fetch Inventory ERP]
    B --> C2[Step 2: Fetch Sales Data CRM]
    B --> C3[Step 3: Currency Conversion]

    %% Parallel execution
    C1 --> D1[Step 4: Aggregate Inventory + Sales] 
    C2 --> D1
    C3 --> D2[Step 5: Apply VAT & Tax Rules]

    %% Dependent step
    D1 --> E1[Step 6: Generate Summary Report]
    D2 --> E1

    %% Policy checks & Observability
    B --> F[Plan Validator & Policy Engine]
    F --> B
    C1 --> G[Observability Layer]
    C2 --> G
    C3 --> G
    D1 --> G
    D2 --> G
    E1 --> G

    %% Final response
    E1 --> H[Aggregated Response to User]
```
---

## Diagram Explanation

1. Planner generates DAG from the user request.
2. Parallel Steps (C1, C2, C3)
   - Independent steps can execute concurrently.
   - Tool executions are deterministic.
3. Dependent Steps (D1, D2 → E1)
   - Wait for required predecessors.
   - Data from prior steps is interpolated.
4. Policy Validation
   - Ensures steps are allowed before execution.
   - Can block or prune unsafe steps.
5. Observability
   - Logs every step input/output, execution time, and failures.
6. Aggregated Response
   - Combines all outputs and LLM summaries into the final response.