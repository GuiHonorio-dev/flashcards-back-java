package br.com.honorio.flashcards.dto.Deck;

import jakarta.validation.constraints.NotNull;

public record UpdateDeckRequestDto(@NotNull String name) {}
