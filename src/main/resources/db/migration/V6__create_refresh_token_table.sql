CREATE TABLE refresh_tokens (
  id VARCHAR(36) PRIMARY KEY,
  student_id VARCHAR(36) NOT NULL REFERENCES students(id),
  token_hash VARCHAR(512) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  revoked BOOLEAN NOT NULL
);

CREATE INDEX idx_refresh_tokens_student_id ON refresh_tokens(student_id);