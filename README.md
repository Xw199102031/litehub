# litehub — Admin UI & Usage

This document describes the Admin management UI updates: a polished Admin Console that includes policy management and an audit-log browser with pagination.

## What changed
- Replaced the simple admin page with a responsive Admin Console (`/admin/permissions.html`) that has two tabs:
  - Policies: list/add/delete Casbin policies, reload enforcer
  - Audit Logs: browse audit logs with server-side pagination and simple filtering
- The UI uses the existing Admin APIs (requires `ADMIN` role):
  - GET /api/admin/casbin/policies
  - POST /api/admin/casbin/policies
  - DELETE /api/admin/casbin/policies
  - POST /api/admin/casbin/reload
  - GET /api/admin/audit-logs

## How to use the Admin UI
1. Log in as an Admin user via the /api/auth/login endpoint and obtain the JWT token.
2. In your browser's console, set the token in localStorage:

   ```js
   localStorage.setItem('litehub_token', '<YOUR_JWT_TOKEN>');
   ```

3. Open the Admin Console at:

   ```
   http://localhost:8080/admin/permissions.html
   ```

4. Policies tab
   - Add a policy by filling Subject / Object / Action and clicking `Add Policy`.
   - Delete a policy via the Delete button on the row.
   - Click `Reload` to force the enforcer to reload policies from persistent storage.

5. Audit Logs tab
   - Use filters (userId or action) and pagination controls to browse audit events.
   - Page size and navigation are available.

## Notes for Operators
- The Admin Console is a demo UI for convenience. For production, restrict access to Admins and protect the UI (e.g. VPN, IP allowlist, MFA).
- The UI expects the Admin APIs to return data in the `ApiResponse` envelope (i.e., `.data` contains payload). If you customized those responses, update the UI accordingly.

## Next steps / Improvements
- Add authentication for the UI page and server-side session handling.
- Add detailed audit log viewing (drilldowns) and CSV export.
- Add search and filtering for policies by subject/object/action.
- Integrate with role-management pages (assign roles/users) in the same Admin console.


