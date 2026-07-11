package br.com.honorio.flashcards.dto.Card;

import jakarta.validation.constraints.NotBlank;

public record CreateCardRequestDto(
    @NotBlank String answer,
    @NotBlank String question,
    @NotBlank String deckId) {}
