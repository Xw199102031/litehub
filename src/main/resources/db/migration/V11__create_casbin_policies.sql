CREATE TABLE IF NOT EXISTS casbin_policy (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  subject VARCHAR(255) NOT NULL,
  object VARCHAR(255) NOT NULL,
  action VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (subject, object, action)
);

CREATE INDEX IF NOT EXISTS idx_casbin_policy_sub ON casbin_policy(subject);
CREATE INDEX IF NOT EXISTS idx_casbin_policy_obj ON casbin_policy(object);
CREATE INDEX IF NOT EXISTS idx_casbin_policy_act ON casbin_policy(action);
