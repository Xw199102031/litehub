INSERT IGNORE INTO permissions (name, description) VALUES
  ('PROJECT_READ', 'Read project metadata');
INSERT IGNORE INTO permissions (name, description) VALUES
  ('PROJECT_WRITE', 'Modify project metadata');
INSERT IGNORE INTO permissions (name, description) VALUES
  ('PROJECT_DELETE', 'Delete project');
INSERT IGNORE INTO permissions (name, description) VALUES
  ('MEMBER_MANAGE', 'Add/remove/change project members');
INSERT IGNORE INTO permissions (name, description) VALUES
  ('TASK_CREATE', 'Create tasks');
INSERT IGNORE INTO permissions (name, description) VALUES
  ('TASK_UPDATE', 'Update tasks');
INSERT IGNORE INTO permissions (name, description) VALUES
  ('TASK_DELETE', 'Delete tasks');
INSERT IGNORE INTO permissions (name, description) VALUES
  ('TASK_ASSIGN', 'Assign tasks');

INSERT IGNORE INTO roles (name, description) VALUES
  ('OWNER', 'Project owner - full control');
INSERT IGNORE INTO roles (name, description) VALUES
  ('MAINTAINER', 'Project maintainer - manage tasks and edit');
INSERT IGNORE INTO roles (name, description) VALUES
  ('MEMBER', 'Project member - contributor');
INSERT IGNORE INTO roles (name, description) VALUES
  ('VIEWER', 'Read-only');
INSERT IGNORE INTO roles (name, description) VALUES
  ('ADMIN', 'Global admin user');

-- OWNER: all perms
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.name IN ('PROJECT_READ','PROJECT_WRITE','PROJECT_DELETE','MEMBER_MANAGE','TASK_CREATE','TASK_UPDATE','TASK_DELETE','TASK_ASSIGN') WHERE r.name = 'OWNER';

-- MAINTAINER
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.name IN ('PROJECT_READ','PROJECT_WRITE','TASK_CREATE','TASK_UPDATE','TASK_DELETE','TASK_ASSIGN') WHERE r.name = 'MAINTAINER';

-- MEMBER
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.name IN ('PROJECT_READ','TASK_CREATE','TASK_UPDATE') WHERE r.name = 'MEMBER';

-- VIEWER
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.name IN ('PROJECT_READ') WHERE r.name = 'VIEWER';

-- ADMIN
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.name IN ('PROJECT_READ','PROJECT_WRITE','PROJECT_DELETE','MEMBER_MANAGE','TASK_CREATE','TASK_UPDATE','TASK_DELETE','TASK_ASSIGN') WHERE r.name = 'ADMIN';
