package br.com.honorio.flashcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.honorio.flashcards.model.CardReview;

public interface ICardReviewRepository extends JpaRepository<CardReview, String> {
  long countByStudentId(String studentId);
}
