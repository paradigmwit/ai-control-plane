```mermaid
flowchart TD
    
%% User interaction
A[User Request] --> B[API Layer]

    %% Planner
    B --> C[Planner]
    C --> D[Plan Validator]

    %% Policy layer
    D --> E[Policy Engine]
    E --> F{Policy OK?}
    F -- Yes --> G[Execution Engine]
    F -- No --> H[Policy Rejection Response]

    %% Execution engine and tools
    G --> I[Tool Registry]
    G --> J[Deterministic Tools]
    J --> G

    %% Observability layer
    G --> K[Observability Layer]
    C --> K
    D --> K
    E --> K

    %% Final Response
    G --> L[Aggregated Response to User]
    H --> L

    %% Notes
    classDef module fill:#f2f2f2,stroke:#333,stroke-width:1px;
    class B,C,D,E,F,G,I,J,K,L module;
```

---


## Diagram Explanation

### User Request → API Layer 
- Validates incoming request, passes to Planner.

### Planner (LLM) → Plan Validator
- Planner generates structured DAG of steps.
- Validator enforces schema and tool availability.

### Policy Engine → Execution Engine
- Checks permissions, cost ceilings, sensitive tools.
- Can reject the plan (H) or allow execution (G).

### Execution Engine
- Resolves DAG dependencies
- Executes deterministic tools
- Handles retries, fallbacks
- Interacts with Tool Registry for metadata
- Logs everything to Observability Layer

### Observability Layer
- Collects all traces:
  - Planner output
  - Validation results
  - Policy decisions
  - Tool inputs/outputs
  - Latency and token usage

### Aggregated Response
- Combines tool outputs and LLM summaries
- Returns to the user
- Includes confidence and policy metadata