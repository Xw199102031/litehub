INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON upper(u.role) = r.name
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO project_roles (project_id, user_id, role_id)
SELECT pm.project_id, pm.user_id, r.id
FROM project_members pm
JOIN roles r ON upper(pm.role) = r.name
ON CONFLICT (project_id, user_id) DO NOTHING;
