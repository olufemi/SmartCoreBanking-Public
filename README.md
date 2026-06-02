# SmartCore Banking

Open-source wallet and ledger infrastructure for digital financial services.

SmartCore helps fintech teams, wallet providers, agency banking platforms, and financial software builders evaluate a modular foundation for wallet, ledger, account, session, gateway, and backoffice workflows. The Community Edition is built for learning, local evaluation, contribution, and public-safe experimentation without exposing production deployment material.

## What Problem SmartCore Solves

Digital financial service teams often need wallet and ledger infrastructure before they are ready to buy or build a full banking platform. SmartCore gives evaluators a modular Java service codebase, a lightweight Community Portal, and a local demo path for understanding wallet and ledger behavior quickly.

SmartCore is not positioned as a universal core banking replacement. It is a practical open-source foundation for wallet and ledger workflows that can grow into managed implementation, support, and enterprise delivery work.

## Community Edition Scope

Included:

- modular Java service source code
- service discovery
- API gateway
- session/authentication service
- profiling and backoffice domain service
- general ledger service
- shared utility service
- lightweight Community Edition portal
- local development, configuration, architecture, demo, and smoke-test documentation

Not included:

- production credentials
- customer-specific data
- private deployment playbooks
- commercial delivery assets
- enterprise support runbooks
- internal network endpoints
- real environment configuration

## Modules

| Module | Purpose |
|---|---|
| `com-smart-core-centralized-wallet-api-discovery` | service discovery |
| `com-smart-core-centralized-wallet-api-gateway` | API gateway |
| `com-smart-core-centralized-wallet-api-sessionmanager` | session and authentication workflows |
| `com-smart-core-centralized-wallet-api-profiling` | profiling, backoffice, and portal-facing workflows |
| `com-smart-core-centralized-wallet-api-general-ledger` | wallet, posting, balance, and transaction workflows |
| `com-smart-core-centralized-wallet-api-utility` | shared utility workflows |
| `smart-core-admin-portal` | static Community Edition demo portal |

## Run The Community Demo

Start with the public demo guide:

- [Run SmartCore Locally](docs/demo.md)
- [Architecture](docs/architecture.md)
- [API Smoke Tests](docs/api-smoke-tests.md)
- [Screenshots](docs/screenshots/)
- [Pilot Readiness](docs/pilot-readiness.md)

The shortest evaluator path is:

1. Build the public modules.
2. Start a disposable local database.
3. Start Profiling and General Ledger with local-only placeholder configuration.
4. Serve the Community Portal on localhost.
5. Run the ten-check wallet and ledger smoke flow.

## Documentation

- [Documentation Index](docs/README.md)
- [Local Development](docs/local-development.md)
- [Configuration](docs/configuration.md)
- [Community Edition Scope](docs/community-edition-scope.md)
- [Security Policy](SECURITY.md)
- [Contributing](CONTRIBUTING.md)

## Security

Do not commit secrets, tokens, passwords, private keys, customer data, internal hosts, or deployment credentials. Use localhost and disposable demo data for evaluation. See [SECURITY.md](SECURITY.md) for vulnerability reporting guidance.

## Commercial And Enterprise Boundary

Some deployment playbooks, customer onboarding assets, support workflows, private integrations, enterprise hardening materials, and commercial delivery assets are intentionally outside this public repository.

## License

Apache 2.0 intended / pending third-party dependency legal review. See [LICENSE](LICENSE) and [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md).
