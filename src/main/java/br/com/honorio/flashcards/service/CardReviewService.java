package br.com.honorio.flashcards.service;

import org.springframework.stereotype.Service;

import br.com.honorio.flashcards.dto.Stats.SetReviewRequestDto;
import br.com.honorio.flashcards.exception.NotFoundException;
import br.com.honorio.flashcards.model.Card;
import br.com.honorio.flashcards.model.CardReview;
import br.com.honorio.flashcards.model.Student;
import br.com.honorio.flashcards.repository.ICardRepository;
import br.com.honorio.flashcards.repository.ICardReviewRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardReviewService {
  
  private final ICardReviewRepository cardReviewRepository;
  private final ICardRepository cardRepository;
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
  }

 

}
