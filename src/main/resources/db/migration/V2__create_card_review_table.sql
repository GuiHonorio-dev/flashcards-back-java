CREATE TABLE card_reviews (
    id VARCHAR(36) PRIMARY KEY,
    student_id VARCHAR(36) NOT NULL REFERENCES students(id),
    card_id VARCHAR(36) NOT NULL REFERENCES cards(id),
    difficulty SMALLINT NOT NULL CHECK (difficulty BETWEEN 1 AND 5),
    reviewed_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_card_reviews_student_id ON card_reviews(student_id);
CREATE INDEX idx_card_reviews_card_id ON card_reviews(card_id);
