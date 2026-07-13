package br.com.honorio.flashcards.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  @Query(value = """
     SELECT c FROM Card c
     LEFT JOIN CardProgress cp ON cp.card = c AND cp.student.id = :studentId
     WHERE c.deck.student.id = :studentId AND (cp IS NULL OR cp.due_at <= :now)
     ORDER BY cp.due_at ASC NULLS FIRST
  """)
  List<Card> findDueCards(@Param("studentId") String studentId, @Param("now") Instant now);
}
