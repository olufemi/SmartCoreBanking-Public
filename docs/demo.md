# Run SmartCore Locally

This guide gives a public-safe path for understanding the SmartCore Community Edition demo. It focuses on the minimum wallet and ledger proof: local database, Profiling service, General Ledger service, Community Portal, and API smoke checks.

The demo is intended for local evaluation only. Use placeholder values, localhost endpoints, and disposable data. Do not use production, staging, customer, or internal environment values.

## What The Demo Shows

- Community Portal dashboard view
- local portal login/session flow
- wallet/account availability
- wallet credit
- wallet debit
- account information lookup
- ledger summary
- ledger transaction history

## Required Local Services

| Service | Purpose |
|---|---|
| Local database | Stores disposable demo wallet, ledger, and portal-facing data. |
| Profiling service | Handles portal-facing and account/customer domain calls. |
| General Ledger service | Handles wallet, posting, balance, summary, and transaction history calls. |
| Community Portal | Static local portal for evaluator walkthroughs. |

Discovery, gateway, session manager, and utility services are included in the source tree, but the shortest evaluator demo can run directly against Profiling and General Ledger.

## Suggested Evaluation Path

1. Read the [Architecture](architecture.md) page.
2. Review the [Configuration](configuration.md) placeholders.
3. Build the project using [Local Development](local-development.md).
4. Start a disposable local database.
5. Start Profiling and General Ledger with local-only configuration.
6. Serve the Community Portal locally.
7. Run the smoke checks described in [API Smoke Tests](api-smoke-tests.md).
8. Compare your result with the public screenshots in [Screenshots](screenshots/).

## Community Portal

The Community Portal is a static demo shell located at:

```text
smart-core-admin-portal/
```

For a simple local visual review, serve that directory on localhost with any static file server. Example:

```bash
cd smart-core-admin-portal
python3 -m http.server 4173 --bind 127.0.0.1
```

Then open:

```text
http://127.0.0.1:4173/
```

## Demo Safety Rules

- Use localhost only.
- Use disposable demo data only.
- Use placeholder secrets only.
- Do not paste real credentials into examples.
- Do not connect the Community Portal to production, staging, customer, or internal endpoints.

## Expected Result

A successful local evaluation should prove that SmartCore can show a portal view and execute a wallet/ledger API path with local demo data. The public demo is not a production deployment guide and does not include private enterprise deployment material.
