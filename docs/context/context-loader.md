# Context Loader — Edu Core Platform

Use this context for all responses related to this project.

---

## SYSTEM CONTEXT

@system-summary.md

---

## DOMAIN CONTEXT

@domain-overview.md
@business-rules.md

---

## ARCHITECTURE CONTEXT

@hexagonal-architecture.md
@dependency-rules.md

---

## TECHNICAL CONTEXT

@persistence-strategy.md
@api-overview.md
@security-overview.md
@multi-tenancy.md

---

## DECISIONS (ADR)

@../decisions/001-hexagonal.md
@../decisions/002-monolith.md
@../decisions/003-rest.md
@../decisions/004-jwt.md
@../decisions/005-database.md
@../decisions/006-java-gradle.md

---

## INSTRUCTIONS FOR AI

- Respect hexagonal architecture
- Do not introduce framework dependencies in domain
- Follow existing ADR decisions
- Prefer simplicity over overengineering
- Keep code production-ready
- Maintain multi-tenant awareness

---

## RESPONSE STYLE

- Senior-level reasoning
- Concise but complete
- No unnecessary explanations
- Focus on correctness and maintainability