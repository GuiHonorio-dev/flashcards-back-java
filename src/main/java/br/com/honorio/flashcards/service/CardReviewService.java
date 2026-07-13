package br.com.honorio.flashcards.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import br.com.honorio.flashcards.dto.Stats.SetReviewRequestDto;
import br.com.honorio.flashcards.exception.NotFoundException;
import br.com.honorio.flashcards.model.Card;
import br.com.honorio.flashcards.model.CardProgress;
import br.com.honorio.flashcards.model.CardReview;
import br.com.honorio.flashcards.model.Student;
import br.com.honorio.flashcards.repository.ICardProgressRepository;
import br.com.honorio.flashcards.repository.ICardRepository;
import br.com.honorio.flashcards.repository.ICardReviewRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardReviewService {

  private static final double MIN_EASE_FACTOR = 1.3;

  private final ICardReviewRepository cardReviewRepository;
  private final ICardRepository cardRepository;
  private final ICardProgressRepository cardProgressRepository;

  public long countByStudentId(String studentId) {
    return cardReviewRepository.countByStudentId(studentId);
  }

  public void setReview(SetReviewRequestDto reviewDto, String studentId) {
    Card card = cardRepository.findById(reviewDto.cardId()).orElseThrow(() -> new NotFoundException("Carta não encontrada"));
    Student student = card.getDeck().getStudent();
    if(!student.getId().equals(studentId)) {
      throw new NotFoundException("Carta não encontrada");
    }

    CardReview review = CardReview.builder()
    .card(card)
    .student(student)
    .difficulty(reviewDto.difficulty())
    .build();

    cardReviewRepository.save(review);

    CardProgress progress = cardProgressRepository.findByStudentIdAndCardId(studentId, card.getId())
    .orElseGet(() -> CardProgress.builder().student(student).card(card).build());

    applySm2(progress, reviewDto.difficulty());

    cardProgressRepository.save(progress);
  }

  private void applySm2(CardProgress progress, int difficulty) {
    int quality = 6 - difficulty;
    
    if (quality < 3) {
      progress.setRepetitions(0);
      progress.setInterval_days(1);
    } else {
      int repetitions = progress.getRepetitions();
      int interval;
      if (repetitions == 0) {
        interval = 1;
      } else if (repetitions == 1) {
        interval = 6;
      } else {
        interval = (int) Math.round(progress.getInterval_days() * progress.getEase_factor());
      }
      progress.setInterval_days(interval);
      progress.setRepetitions(repetitions + 1);
    }

    double easeFactor = progress.getEase_factor() + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
    progress.setEase_factor(Math.max(MIN_EASE_FACTOR, easeFactor));

    Instant now = Instant.now();
    progress.setLast_reviewed_at(now);
    progress.setDue_at(now.plus(progress.getInterval_days(), ChronoUnit.DAYS));
  }

}
