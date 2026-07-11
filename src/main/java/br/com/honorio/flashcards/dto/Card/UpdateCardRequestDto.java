package br.com.honorio.flashcards.dto.Card;

public record UpdateCardRequestDto(String question, String answer, String deckId) {}
