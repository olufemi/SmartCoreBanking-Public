# Configuration

SmartCore Banking Community Edition does not include live environment configuration. Configure services with local environment variables or private local files that are not committed.

## Required Configuration Areas

Each local environment should provide values for:

- database URL, username, and password
- Redis host, port, and password if Redis features are enabled
- JWT signing secret
- service-to-service admin API keys
- portal JWT secret
- allowed local portal origins
- local service ports

## Example Environment Variables

Use placeholder values like these for local development:

```bash
export SMARTCORE_DATASOURCE_URL="jdbc:postgresql://localhost:5432/smartcore_local"
export SMARTCORE_DATASOURCE_USERNAME="smartcore_user"
export SMARTCORE_DATASOURCE_PASSWORD="change-me-local-only"

export SMARTCORE_REDIS_HOST="localhost"
export SMARTCORE_REDIS_PORT="6379"
export SMARTCORE_REDIS_PASSWORD="change-me-local-only"

export SMARTCORE_JWT_SECRET="replace-with-local-development-secret"
export SMARTCORE_BACKOFFICE_ADMIN_API_KEY="replace-with-local-admin-key"
export SMARTCORE_LEDGER_INTERNAL_ADMIN_API_KEY="replace-with-local-ledger-key"
export SMARTCORE_LEDGER_APPROVAL_ADMIN_API_KEY="replace-with-local-approval-key"
export SMARTCORE_BACKOFFICE_PORTAL_JWT_SECRET="replace-with-local-portal-secret"

export SMARTCORE_PORTAL_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:4173"
```

These values are examples only. Do not reuse them outside a disposable local environment.

## Configuration Keys Referenced in Source

Some source files reference Spring configuration keys such as:

```text
gen.jwt.secret-key
gen.redis.password
smartcore.backoffice.admin.api-key
smartcore.backoffice.portal.jwt-secret
smartcore.ledger.internal.admin-api-key
smartcore.ledger.approval.admin-api-key
smartcore.portal.allowed-origins
```

These are configuration keys, not bundled secret values. Provide local values through your own environment.

## What Must Not Be Committed

Do not commit:

- real passwords
- JWT secrets
- API keys
- private keys
- live database URLs
- internal IP addresses
- production, staging, or customer endpoint values
- customer names or customer-specific data
- deployment credentials

## Recommended Local Pattern

Keep local overrides in files ignored by Git, or export them in your shell before running a module. When adding examples to this repository, use `.example` or `.sample` files with obvious placeholder values only.
