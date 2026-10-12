INSERT INTO permissions (name, description) VALUES
  ('PROJECT_READ', 'Read project metadata') ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description) VALUES
  ('PROJECT_WRITE', 'Modify project metadata') ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description) VALUES
  ('PROJECT_DELETE', 'Delete project') ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description) VALUES
  ('MEMBER_MANAGE', 'Add/remove/change project members') ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description) VALUES
  ('TASK_CREATE', 'Create tasks') ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description) VALUES
  ('TASK_UPDATE', 'Update tasks') ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description) VALUES
  ('TASK_DELETE', 'Delete tasks') ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description) VALUES
  ('TASK_ASSIGN', 'Assign tasks') ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES
  ('OWNER', 'Project owner - full control') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description) VALUES
  ('MAINTAINER', 'Project maintainer - manage tasks and edit') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description) VALUES
  ('MEMBER', 'Project member - contributor') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description) VALUES
  ('VIEWER', 'Read-only') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description) VALUES
  ('ADMIN', 'Global admin user') ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
  'PROJECT_READ','PROJECT_WRITE','PROJECT_DELETE','MEMBER_MANAGE','TASK_CREATE','TASK_UPDATE','TASK_DELETE','TASK_ASSIGN'
)
WHERE r.name = 'OWNER'
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
  'PROJECT_READ','PROJECT_WRITE','TASK_CREATE','TASK_UPDATE','TASK_DELETE','TASK_ASSIGN'
)
WHERE r.name = 'MAINTAINER'
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
  'PROJECT_READ','TASK_CREATE','TASK_UPDATE'
)
WHERE r.name = 'MEMBER'
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN ('PROJECT_READ')
WHERE r.name = 'VIEWER'
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
  'PROJECT_READ','PROJECT_WRITE','PROJECT_DELETE','MEMBER_MANAGE','TASK_CREATE','TASK_UPDATE','TASK_DELETE','TASK_ASSIGN'
)
WHERE r.name = 'ADMIN'
ON CONFLICT (role_id, permission_id) DO NOTHING;
