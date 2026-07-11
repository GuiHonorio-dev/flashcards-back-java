package br.com.honorio.flashcards.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.honorio.flashcards.model.Deck;

public interface IDeckRepository extends JpaRepository<Deck, String> {
  Optional<Deck> findByName(String name);
  
  Optional<Deck> findById(String id);

  List<Deck> findByStudentId(String studentId);

  Optional<Deck> findByIdAndStudentId(String deckId, String studentId);
}
