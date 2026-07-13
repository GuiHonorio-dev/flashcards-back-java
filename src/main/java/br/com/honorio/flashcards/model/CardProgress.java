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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "card_progress")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CardProgress {
  
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne()
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne()
  @JoinColumn(name = "card_id")
  private Card card;
  
  @Column(nullable = false)
  @Builder.Default
  private Integer repetitions = 0;

  @Column(nullable = false)
  @Builder.Default
  private Double ease_factor = 2.50;

  @Column(nullable = false)
  @Builder.Default
  private Integer interval_days = 0;

  @Column(nullable = false)
  private Instant due_at;

  private Instant last_reviewed_at;

  @Column(name = "created_at", nullable = false)
  private Instant created_at;

  @Column(name = "updated_at", nullable = false)
  private Instant updated_at;

  @PrePersist
  protected void onCreate() {
    Instant now = Instant.now();
    created_at = now;
    updated_at = now;
  }

  @PreUpdate
  protected void onUpdate() {
    updated_at = Instant.now();
  }

}
