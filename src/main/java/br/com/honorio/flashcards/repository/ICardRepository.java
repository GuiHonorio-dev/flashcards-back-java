package br.com.honorio.flashcards.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.honorio.flashcards.model.Card;

public interface ICardRepository extends JpaRepository<Card, String> {
  Optional<Card> findById(String id);
  


  @Query(value = """
  SELECT c FROM Card c
  WHERE c.deck.id = :deckId AND c.deck.student.id = :studentId   
  """)
  List<Card> findByDeckId(String deckId, String studentId);

  @Query(value = """
     SELECT c FROM Card c
     WHERE c.id = :cardId AND c.deck.student.id = :studentId 
  """)
  Optional<Card> findByIdAndStudentId(String cardId, String studentId);
}
