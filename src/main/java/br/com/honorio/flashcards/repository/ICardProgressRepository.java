package br.com.honorio.flashcards.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.honorio.flashcards.model.CardProgress;

public interface ICardProgressRepository extends JpaRepository<CardProgress, String> {
  Optional<CardProgress> findByStudentIdAndCardId(String studentId, String cardId);
}
