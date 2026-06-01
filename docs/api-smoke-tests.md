# API Smoke Tests

SmartCore Community Edition has a public-safe smoke-test story for the local wallet and ledger demo. The checks below summarize the validated evaluator path without exposing private runbooks, credentials, customer data, or deployment details.

## Smoke Check Summary

| Check | Purpose |
|---|---|
| `portal-login` | Confirms a local demo portal login/session path can be exercised. |
| `portal-session` | Confirms the local portal session can be read after login. |
| `portal-dashboard` | Confirms dashboard-facing data can be loaded. |
| `portal-health` | Confirms portal-facing service availability. |
| `wallet-exists` | Confirms a neutral demo wallet/account can be found. |
| `wallet-credit` | Confirms a local credit transaction can be posted. |
| `wallet-debit` | Confirms a local debit transaction can be posted. |
| `account-info` | Confirms account information can be queried. |
| `ledger-summary` | Confirms ledger summary data can be queried. |
| `ledger-transactions` | Confirms transaction history can be queried. |

## Expected Local Result

The expected local result is ten passing checks:

```text
PASS portal-login
PASS portal-session
PASS portal-dashboard
PASS portal-health
PASS wallet-exists
PASS wallet-credit
PASS wallet-debit
PASS account-info
PASS ledger-summary
PASS ledger-transactions
SmartCore local live demo smoke test completed successfully.
```

## What These Checks Prove

These checks prove a minimum Community Edition flow:

- a portal-facing service can authenticate a local demo session
- local dashboard data is reachable
- wallet/account state can be read
- credit and debit transactions can be posted
- ledger summary and transaction history can be queried

## What These Checks Do Not Prove

The public smoke checks are not a production certification. They do not prove:

- production scalability
- regulated deployment readiness
- customer-specific integration readiness
- enterprise monitoring, disaster recovery, or security operations
- commercial support readiness

Those areas belong to controlled pilot, enterprise, and commercial delivery work.
