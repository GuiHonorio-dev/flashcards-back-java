package br.com.honorio.flashcards.dto.Deck;

import jakarta.validation.constraints.NotBlank;

public record CreateDeckRequestDto(
    @NotBlank String name) {}
