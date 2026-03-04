```mermaid

flowchart TD
    %% User input
    A[User Request] --> B[API Layer: Validation & Routing]

    %% Planning
    B --> C[Planner LLM generates Execution Plan / DAG]

    %% Plan Validation
    C --> D[Plan Validator]
    D --> E[Policy Engine]
    E --> F{Policy OK?}
    F -- Yes --> G[Execution Engine]
    F -- No --> H[Return Policy Rejection Response]

    %% DAG Execution - Parallel & Dependent Steps
    G --> I1[Step 1: Tool / API Call]
    G --> I2[Step 2: Tool / API Call]
    G --> I3[Step 3: Tool / API Call]

    %% Dependent aggregation
    I1 --> J[Step 4: Aggregate / Compute]
    I2 --> J
    I3 --> K[Step 5: Optional Parallel Step]
    K --> J

    %% Retry / Fallback handling
    I1 --> L1{Success?}
    L1 -- No --> M1[Retry Step 1]
    M1 --> L1
    L1 -- Failed after retries --> N1[Fallback / Default]
    N1 --> J

    I2 --> L2{Success?}
    L2 -- No --> M2[Retry Step 2]
    M2 --> L2
    L2 -- Failed after retries --> N2[Fallback / Default]
    N2 --> J

    %% Observability Layer
    C --> O[Observability: Log Planner Output]
    D --> O
    E --> O
    I1 --> O
    I2 --> O
    I3 --> O
    J --> O

    %% Final Response
    J --> P[Aggregated Response to User]
    H --> P

    %% Tool Registry / Deterministic Tools
    I1 --> Q[Tool Registry / Tools]
    I2 --> Q
    I3 --> Q

    %% Class styling
    classDef module fill:#f2f2f2,stroke:#333,stroke-width:1px;
    class B,C,D,E,F,G,H,I1,I2,I3,J,K,L1,L2,M1,M2,N1,N2,O,P,Q module;
```
---

## Diagram Explanation

1. User Request → API Layer: validates and routes request.
2. Planner: generates structured DAG with dependencies.
3. Plan Validator & Policy Engine: ensures plan is valid, safe, and within cost/permissions.
4. Execution Engine: handles parallel steps, dependent steps, retries, and fallbacks.
5. Tool Registry: deterministic tools execute all non-LLM logic.
6. Observability Layer: logs all reasoning, plan validation, execution steps, retries, fallbacks, and metrics.
7. Aggregated Response: returned to user after execution completes (or rejected if policy fails).

This diagram combines all three previous flows (standard, parallel, retry/fallback) into a single comprehensive view of the system lifecycle.