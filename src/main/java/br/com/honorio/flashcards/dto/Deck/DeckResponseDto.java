package br.com.honorio.flashcards.dto.Deck;

import java.time.Instant;

public record DeckResponseDto(String id, String name,Instant reviewed_at, Instant createdAt) {
  
}
