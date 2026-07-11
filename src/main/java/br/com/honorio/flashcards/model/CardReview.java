package br.com.honorio.flashcards.model;

import java.time.Instant;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "card_reviews")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CardReview {
  
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne(optional = false)
  @JoinColumn(name = "card_id")
  private Card card;

  @Column(name = "reviewed_at", nullable = false)
  private Instant reviewedAt;

  @Column(nullable = false)
  private Integer difficulty;

  @PrePersist
  private void onCreate() {
    reviewedAt = Instant.now();
  }

}
