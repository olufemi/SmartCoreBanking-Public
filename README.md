# SmartCore Banking

SmartCore Banking is a modular open-source banking core for wallet, ledger, session, profiling, gateway, discovery, and shared utility workflows.

This repository is the SmartCore Community Edition. It is intended for learning, review, contribution, and community-led improvement. It includes public-safe service source code and a lightweight demo admin portal, but it does not include live deployment material, credentials, customer-specific data, or commercial delivery assets.

## Modules

- `com-smart-core-centralized-wallet-api-discovery` - service discovery
- `com-smart-core-centralized-wallet-api-gateway` - API gateway
- `com-smart-core-centralized-wallet-api-general-ledger` - ledger service
- `com-smart-core-centralized-wallet-api-profiling` - profiling and backoffice domain service
- `com-smart-core-centralized-wallet-api-sessionmanager` - session and authentication service
- `com-smart-core-centralized-wallet-api-utility` - shared utility service
- `smart-core-admin-portal` - static Community Edition demo portal

## Quick Start

Start with the local development guide:

- [Local Development](docs/local-development.md)
- [Configuration](docs/configuration.md)
- [Community Edition Scope](docs/community-edition-scope.md)

## Security

Do not commit secrets, tokens, passwords, private keys, customer data, internal hosts, or deployment credentials. See [SECURITY.md](SECURITY.md) for vulnerability reporting guidance.

## Commercial and Private Editions

Some enterprise delivery material, deployment playbooks, private integrations, customer onboarding assets, and commercial support workflows are intentionally outside this public repository.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).
