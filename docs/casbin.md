# Casbin integration for LiteHub

This document explains how Casbin is integrated into LiteHub and how to manage policies.

## Overview
- Casbin provides an extensible policy engine for enforcing fine-grained RBAC.
- LiteHub loads a Casbin model from `src/main/resources/casbin/model.conf` and a policy file from the path configured by `casbin.policy.path` in `application.properties` (default: `config/casbin/policy.csv`).
- Admin APIs are provided to list/add/remove policies and to reload the enforcer.

## Policy storage
- By default policies are stored in a file (config/casbin/policy.csv). In production you should use a persistent adapter (JDBC adapter) or a centralized policy service.

## Admin APIs
Requires ADMIN role.

- GET /api/admin/casbin/policies
  - List all policies

- POST /api/admin/casbin/policies
  - Body: {"subject":"MAINTAINER","object":"task","action":"create"}
  - Adds a policy and persists it

- DELETE /api/admin/casbin/policies?subject=MAINTAINER&object=task&action=create
  - Removes the policy

- POST /api/admin/casbin/reload
  - Reload policies from storage

## Production recommendations
- Use a persistent Casbin adapter (JDBC / etcd / etc.) and store policies in the database.
- Protect Admin APIs with strong security and audit all changes.
- Consider multi-region synchronization of policies when running multiple instances.

