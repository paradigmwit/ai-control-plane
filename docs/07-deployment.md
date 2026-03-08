# 07 – Deployment

## 1. Deployment Targets

- **Development:** H2 in-memory DB, local Ollama runtime
- **Staging/Production:** PostgreSQL, multiple LLM providers
- **Containers:** Dockerized Spring Boot application

---

## 2. Configuration Strategy

- Profiles: dev, staging, prod
- LLM providers configurable via `application.yaml`
- DB schema configured via Liquibase
- Logging & metrics endpoints enabled via Spring Boot Actuator

---

## 3. Operational Considerations

- Database migrations run on startup with Liquibase
- Secrets (API keys, credentials) injected via environment variables
- Observability metrics exported to Prometheus/Grafana
- LLM endpoints monitored for latency and cost