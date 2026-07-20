package br.com.honorio.flashcards.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.honorio.flashcards.model.Card;
import br.com.honorio.flashcards.model.CardReview;

public interface ICardReviewRepository extends JpaRepository<CardReview, String> {
  long countByStudentId(String studentId);

  @Query(value = """
     SELECT cr.card FROM CardReview cr
     WHERE cr.student.id = :studentId AND cr.difficulty >= :minDifficulty
     GROUP BY cr.card
     ORDER BY COUNT(cr) DESC
  """)
  List<Card> findMostWrongCards(@Param("studentId") String studentId, @Param("minDifficulty") int minDifficulty);
}


