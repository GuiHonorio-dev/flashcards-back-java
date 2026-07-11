package br.com.honorio.flashcards.dto.Card;

import java.time.Instant;

public record GetCardResponseDto(String id, String question, String answer, Instant createdAt) {
  
}
