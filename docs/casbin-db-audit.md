# Casbin DB-backed policies and Audit Integration

This document explains how LiteHub now persists Casbin policies to the database and records audit logs for all policy and role changes.

## New database table
- `casbin_policy(subject, object, action, created_at)` — stores unique policies
- Migration file: `V11__create_casbin_policies.sql`

## How it works
- On startup, the CasbinService loads policies from `casbin_policy` into the Enforcer.
- Admin API endpoints add/remove policies via CasbinService which updates both the Enforcer and the DB.
- Each policy add/remove is recorded into `audit_logs` with actor, IP, UA, and details.

## Admin UI
- A simple static admin UI is available at `/admin/permissions.html`.
- The UI requires an `Authorization: Bearer <token>` header — store token in browser localStorage as `litehub_token`.

## Audit logs
- All policy changes create an entry in `audit_logs` (table `audit_logs`). Use the Admin Audit endpoint to view logs.

## Next steps
- Use a persistent Casbin adapter for multi-instance setups (JDBC adapter)
- Add pagination and filtering for policies and audit logs in the Admin UI
- Harden Admin API (MFA, IP restrictions)
