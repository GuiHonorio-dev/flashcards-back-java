package br.com.honorio.flashcards.dto.Stats;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SetReviewRequestDto(
  @NotBlank String cardId,
  @NotNull @Min(1) @Max(5) Integer difficulty
) {}
