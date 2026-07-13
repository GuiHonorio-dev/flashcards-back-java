CREATE TABLE card_progress (
  id VARCHAR(36) PRIMARY KEY,
  student_id VARCHAR(36) NOT NULL REFERENCES students(id),
  card_id VARCHAR(36) NOT NULL REFERENCES cards(id),

  repetitions INTEGER NOT NULL DEFAULT 0,
  ease_factor NUMERIC(4, 2) NOT NULL DEFAULT 2.50 CHECK (ease_factor >= 1.30),
  interval_days INTEGER NOT NULL DEFAULT 0,

  due_at TIMESTAMP NOT NULL,
  last_reviewed_at TIMESTAMP,

  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,

  CONSTRAINT uk_card_progress_student_card UNIQUE (student_id, card_id)

);

CREATE INDEX idx_card_progress_student_due ON card_progress(student_id, due_at);
CREATE INDEX idx_card_progress_card_id ON card_progress(card_id);