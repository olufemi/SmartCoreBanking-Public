# SmartCore Architecture

SmartCore Banking Community Edition is a modular wallet and ledger platform for digital financial services. It is designed around separate services for discovery, gateway routing, session/authentication, profiling/backoffice workflows, ledger operations, and shared utilities.

This public architecture document intentionally uses local/demo terminology only. It does not describe private deployment topology, customer environments, production credentials, or commercial delivery assets.

## Module Overview

| Module | Role |
|---|---|
| `com-smart-core-centralized-wallet-api-discovery` | Service discovery for local or distributed service registration. |
| `com-smart-core-centralized-wallet-api-gateway` | API gateway entry point for routing requests to SmartCore services. |
| `com-smart-core-centralized-wallet-api-sessionmanager` | Session and authentication-related service workflows. |
| `com-smart-core-centralized-wallet-api-profiling` | Profiling, backoffice, portal-facing, and account/customer domain workflows. |
| `com-smart-core-centralized-wallet-api-general-ledger` | Wallet, ledger, balance, and transaction posting workflows. |
| `com-smart-core-centralized-wallet-api-utility` | Shared utility capabilities used by other services. |
| `smart-core-admin-portal` | Lightweight Community Edition portal for demo and review workflows. |

## Public Community Flow

```text
User / Developer
      |
      v
Community Portal or API Client
      |
      v
Profiling Service  <---->  General Ledger Service
      |                         |
      v                         v
Local Demo Database       Wallet / Ledger Records
```

For the simplest local demo, the portal and smoke tests call the Profiling and General Ledger services directly on localhost. Discovery, gateway, session manager, and utility services remain part of the Community Edition codebase, but the first evaluator path focuses on the minimum wallet/ledger proof.

## Why This Shape

SmartCore separates operational concerns so evaluators can reason about each part independently:

- Profiling handles portal-facing and account/customer domain workflows.
- General Ledger handles wallet/account state, credits, debits, balances, summaries, and transaction history.
- Gateway and discovery support fuller service routing patterns.
- Session manager and utility services support broader platform operations.
- The Community Portal gives a visual way to understand the system without requiring production infrastructure.

## Community Edition Boundary

Community Edition includes public-safe service source code, local development documentation, example configuration guidance, and a lightweight portal. It does not include private deployment playbooks, customer onboarding material, production credentials, enterprise support runbooks, or commercial delivery assets.

See also:

- [Community Edition Scope](community-edition-scope.md)
- [Local Development](local-development.md)
- [Run SmartCore Locally](demo.md)
