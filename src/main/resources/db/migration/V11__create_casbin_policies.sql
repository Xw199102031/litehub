CREATE TABLE IF NOT EXISTS casbin_policy (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  subject VARCHAR(255) NOT NULL,
  object VARCHAR(255) NOT NULL,
  action VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (subject, object, action),
  KEY idx_casbin_policy_sub (subject),
  KEY idx_casbin_policy_obj (object),
  KEY idx_casbin_policy_act (action)
);
