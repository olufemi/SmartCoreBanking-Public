# Local Development

This guide describes how to run the SmartCore Banking Community Edition from a local developer workstation. It uses public-safe placeholder configuration only.

## Prerequisites

- Java 21 or a compatible Java runtime for the modules you are running
- Maven 3.8 or later
- Git
- A local database service that you control
- A local Redis-compatible service if you enable Redis-backed session features
- A browser for the Community Edition portal

Do not use production, staging, customer, or vendor credentials in local development.

## Clone and Build

```bash
git clone https://github.com/olufemi/SmartCoreBanking-Public.git
cd SmartCoreBanking-Public
mvn -DskipTests package
```

For a faster module-level check, build a single module:

```bash
mvn -DskipTests compile -f com-smart-core-centralized-wallet-api-general-ledger/pom.xml
```

## Suggested Startup Order

Start dependencies first, then services:

1. Local database
2. Local Redis-compatible service, if enabled
3. `com-smart-core-centralized-wallet-api-discovery`
4. `com-smart-core-centralized-wallet-api-gateway`
5. `com-smart-core-centralized-wallet-api-sessionmanager`
6. `com-smart-core-centralized-wallet-api-utility`
7. `com-smart-core-centralized-wallet-api-profiling`
8. `com-smart-core-centralized-wallet-api-general-ledger`
9. `smart-core-admin-portal`

The exact service ports are controlled by your local configuration. Keep them on localhost while developing.

## Local URLs

Use local-only URLs such as:

```text
http://localhost:8080
http://localhost:8080/api/profilings
http://localhost:3000
http://localhost:4173
```

These are examples only. Adjust them to match your local module configuration.

## Portal Demo Mode

The Community Edition portal is a static demo shell.

Open:

```text
smart-core-admin-portal/index.html
```

The portal defaults to a localhost profiling API URL and neutral demo data. Use the portal settings field to point it at your own local service endpoint. Do not configure it with production, staging, customer, or internal network endpoints.

## Development Notes

- Keep generated build output out of commits.
- Keep local configuration files private.
- Use placeholder values in examples and tests.
- Add tests when changing behavior.
