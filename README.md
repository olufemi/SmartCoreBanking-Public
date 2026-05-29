# SmartCore Banking

SmartCore Banking is an open-source modular banking platform core. This public distribution contains service source code for wallet-ledger operations, profiling, session management, API gateway, service discovery, and shared utility components.

## Overview

This repository is intended for review, learning, contribution, and community-driven improvement of the SmartCore Banking core services.

## Modules

- `com-smart-core-centralized-wallet-api-discovery` - service discovery
- `com-smart-core-centralized-wallet-api-gateway` - API gateway
- `com-smart-core-centralized-wallet-api-general-ledger` - ledger service
- `com-smart-core-centralized-wallet-api-profiling` - profiling and backoffice domain service
- `com-smart-core-centralized-wallet-api-sessionmanager` - session and authentication service
- `com-smart-core-centralized-wallet-api-utility` - shared utility service

## Requirements

- Java 21 or compatible project runtime
- Maven
- A local database and service configuration supplied by your own environment

## Configuration

This public repository does not include live environment configuration. Use environment variables or local configuration files that are not committed to source control.

Do not commit secrets, tokens, passwords, private keys, customer data, internal hosts, or deployment credentials.

## Running Locally

Review each module's Maven configuration and provide your own local environment settings before running services. Live deployment material and private operational playbooks are intentionally not included in this public distribution.

## Included

- Core service source code
- Public-safe project structure
- Public contribution and security policies

## Not Included

- Production credentials
- Deployment secrets
- Customer-specific material
- Commercial strategy documents
- Private support playbooks
- Enterprise delivery material
- Unsanitized admin portal assets

## Contributing

See `CONTRIBUTING.md`.

## Security

See `SECURITY.md` for vulnerability reporting guidance.

## License

Licensed under the Apache License, Version 2.0. See `LICENSE`.
