# 06 – Evaluation

## 1. Objectives

Evaluation ensures:

- Generated plans are valid and deterministic
- Execution produces expected outputs
- Cost remains within acceptable thresholds
- Policies are enforced correctly

---

## 2. Evaluation Strategy

- Run **automated tests** with sample prompts
- Validate **plan structure** against schema
- Check **step dependencies** for cycles
- Simulate **execution** using deterministic tools
- Compare outputs with expected results

---

## 3. Metrics

| Metric | Purpose |
|--------|---------|
| Plan Validity | JSON schema compliance |
| DAG Correctness | No cycles, dependencies respected |
| Cost Accuracy | LLM + tool costs match estimates |
| Execution Success | Steps completed without errors |
| Latency | Average execution time per step |

---

## 4. Automation

- Implement **JUnit + integration tests**
- Store sample prompts and expected outputs
- Generate **evaluation reports**
- Track historical performance over time