INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON UPPER(u.role) = r.name;

INSERT IGNORE INTO project_roles (project_id, user_id, role_id)
SELECT pm.project_id, pm.user_id, r.id FROM project_members pm JOIN roles r ON UPPER(pm.role) = r.name;
